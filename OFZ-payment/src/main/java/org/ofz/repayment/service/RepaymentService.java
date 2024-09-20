package org.ofz.repayment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.management.entity.StockInformation;
import org.ofz.management.exception.StockInformationNotFoundException;
import org.ofz.management.projection.QuantityAndStockCodeOfMortgagedStock;
import org.ofz.management.repository.MortgagedStockRepository;
import org.ofz.management.repository.StockInformationRepository;
import org.ofz.management.utils.BankCategory;
import org.ofz.management.utils.StockStability;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.payment.exception.payment.PaymentNotFoundException;
import org.ofz.payment.repository.PaymentHistoryRepository;
import org.ofz.rabbitMQ.Publisher;
import org.ofz.rabbitMQ.rabbitDto.RepaymentHistoryLogDTO;
import org.ofz.redis.RedisUtil;
import org.ofz.repayment.dto.request.CashRepaymentRequest;
import org.ofz.repayment.dto.response.*;
import org.ofz.repayment.utils.AccountUtils;
import org.ofz.repayment.RepaymentHistory;
import org.ofz.repayment.RepaymentHistoryRepository;
import org.ofz.repayment.RepaymentType;
import org.ofz.repayment.exception.repayment.InvalidPrepaymentAmountException;
import org.ofz.repayment.exception.repayment.NoRepaymentRecordsException;
import org.ofz.repayment.exception.repayment.TooHighPrepaymentAmountException;
import org.ofz.repayment.exception.user.UserNotFoundException;
import org.ofz.repayment.utils.StockUtils;
import org.ofz.user.User;
import org.ofz.user.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepository userRepository;
    private final StockUtils stockUtils;
    private final RedisUtil redisUtil;
    private final StockInformationRepository stockInformationRepository;
    private final RepaymentHistoryRepository repaymentHistoryRepository;
    private final MortgagedStockRepository mortgagedStockRepository;
    private final AccountUtils accountUtils;
    private final Publisher<RepaymentHistoryLogDTO> publisher;

    public PaymentInfoForCashResponse getPaymentInfo(Long userId) {

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        int thisMonthRepaymentAmount = payment.getPreviousMonthDebt();
        int creditLimit = payment.getCreditLimit();
        final int currentMonthDebt = payment.getCurrentMonthDebt();

        final int totalDebt = payment.getPreviousMonthDebt() + currentMonthDebt;
        final int remainCreditLimit = payment.getCreditLimit() - totalDebt;

        Pageable pageable = PageRequest.of(0, 2);
        List<PaymentHistoriesResponse.PaymentHistoryDTO> currentPaymentHistories = paymentHistoryRepository.findPaymentHistoryByUserId(userId, pageable);

        return PaymentInfoForCashResponse.builder()
                .paymentAmount(thisMonthRepaymentAmount)
                .creditLimit(creditLimit)
                .currentMonthDebt(currentMonthDebt)
                .remainCreditLimit(remainCreditLimit < 0 ? 0 : remainCreditLimit)
                .paymentHistories(currentPaymentHistories)
                .build();
    }

    public RepaymentAccountResponse getPaymentAccount(Long userId) {

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        String accountNumber = payment.getRepaymentAccountNumber();
        int totalDebt = payment.getPreviousMonthDebt() + payment.getCurrentMonthDebt();
        int accountDeposit = getPaymentAccountData(accountNumber).getDeposit();
        AccountResponse response = getPaymentAccountData(accountNumber);
        String companyCode = response.getCompanyCode();

        return RepaymentAccountResponse.builder()
                .totalDebt(totalDebt)
                .accountNumber(response.getAccountNumber())
                .accountName(BankCategory.fromCode(companyCode))
                .companyCode(companyCode)
                .accountDeposit(accountDeposit)
                .build();
    }

    public MonthlyDebtResponse getMonthlyDebt(Long userId) {

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        int repaymentDate = payment.getRepaymentDate();
        int previousMonthDebt = payment.getPreviousMonthDebt();
        int currentMonthDebt = payment.getCurrentMonthDebt();

        return MonthlyDebtResponse.builder()
                .repaymentDate(repaymentDate)
                .previousMonthDebt(previousMonthDebt)
                .currentMonthDebt(currentMonthDebt)
                .build();
    }

    @Transactional
    public CashRepaymentResponse repayWithCash(CashRepaymentRequest cashRepaymentRequest) {

        Long userId = cashRepaymentRequest.getUserId();

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저가 조회되지 않습니다."));

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        int prepaymentAmount = cashRepaymentRequest.getAmount();

        if (prepaymentAmount <= 0) {
            throw new InvalidPrepaymentAmountException("상환할 금액을 다시 설정해주세요.");
        }

        int previousMonthDebt = payment.getPreviousMonthDebt();
        int currentMonthDebt = payment.getCurrentMonthDebt();

        int totalDebt = previousMonthDebt + currentMonthDebt;

        if (totalDebt == 0) {
            throw new NoRepaymentRecordsException("상환할 금액이 없습니다.");
        }

        int calculatedPrepaymentAmount = previousMonthDebt - prepaymentAmount;

        if (calculatedPrepaymentAmount < 0) {

            payment.minusPreviousMonthDebt(previousMonthDebt);

            int secondCalculatedPrepaymentAmount = currentMonthDebt + calculatedPrepaymentAmount;

            if (secondCalculatedPrepaymentAmount < 0) {
                throw new TooHighPrepaymentAmountException("선결제 금액이 너무 높습니다.");
            } else {
                payment.minusCurrentMonthDebt(-calculatedPrepaymentAmount);
            }

        } else {

            payment.minusPreviousMonthDebt(prepaymentAmount);
        }

        String accountNumber = payment.getRepaymentAccountNumber();
        CashRepaymentResponse response = accountUtils.fetchCashRepayment(accountNumber, prepaymentAmount);

        response.setMessage("현금 선결제가 완료되었습니다.");

        LocalDateTime nowTime = LocalDateTime.now();

        RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                .repaymentAmount(prepaymentAmount)
                .createdAt(nowTime)
                .userId(userId)
                .type(RepaymentType.PRE_CASH)
                .build();

        RepaymentHistory savedrepaymentHistory = repaymentHistoryRepository.save(repaymentHistory);

        boolean hasMortgagedStock = mortgagedStockRepository.existsByUserId(userId);
        boolean marginRequirement = true;
        int userCreditLimit = payment.getCreditLimit();

        if (hasMortgagedStock) {
            marginRequirement = checkMarginRequirement(userCreditLimit, userId);
        } else {
            payment.changeCreditLimit(0);
        }

        if (marginRequirement && payment.getOverdueDay() != null) {

            if (payment.isRateFlag()) {
                payment.enablePay();
            }
        }

        if (payment.getTotalDebt() == 0 && !payment.isRateFlag()) {
            payment.enableRateFlag();
        }

        if (payment.getTotalDebt() == 0) {
            payment.resetOverdueDay();
        }

        paymentRepository.save(payment);

        RepaymentHistoryLogDTO log = RepaymentHistoryLogDTO.builder()
                .id(savedrepaymentHistory.getId())
                .loginId(user.getLoginId())
                .amount(prepaymentAmount)
                .accountNumber(accountNumber)
                .type(RepaymentType.PRE_CASH.kor)
                .date(nowTime)
                .build();

        publisher.sendMessage(log);

        return response;
    }

    private AccountResponse getPaymentAccountData(String accountNumber) {
        return accountUtils.fetchPaymentAccount(accountNumber);
    }

    private boolean checkMarginRequirement(int userCreditLimit, Long userId) {

        List<QuantityAndStockCodeOfMortgagedStock> mortgagedStocks = mortgagedStockRepository.findMortgagedStocksByUserId(userId);

        double totalPriceOfPawn = 0;

        for (QuantityAndStockCodeOfMortgagedStock mortgagedStock : mortgagedStocks) {

            String stockCode = mortgagedStock.getStockCode();
            int quantity = mortgagedStock.getQuantity();

            Integer previousPrice = redisUtil.fetchStoredPreviousPrice(stockCode);
            if (previousPrice == null) {
                previousPrice = stockUtils.fetchPreviousStockPrice(stockCode);
            }

            StockInformation stockInformation = stockInformationRepository
                    .findByStockCode(stockCode)
                    .orElseThrow(() -> new StockInformationNotFoundException("증권 정보가 조회되지 않습니다. " + stockCode));

            int stabilityLevel = stockInformation.getStabilityLevel();

            totalPriceOfPawn += quantity * StockStability.calculateLimitPrice(stabilityLevel, previousPrice);
        }

        double ratio = totalPriceOfPawn / userCreditLimit * 100;

        return ratio >= 140;
    }
}
