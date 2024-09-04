package org.ofz.repayment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.payment.exception.payment.PaymentNotFoundException;
import org.ofz.payment.repository.PaymentHistoryRepository;
import org.ofz.repayment.dto.request.CashPrepaymentRequest;
import org.ofz.repayment.dto.response.*;
import org.ofz.repayment.utils.AccountUtils;
import org.ofz.repayment.RepaymentHistory;
import org.ofz.repayment.RepaymentHistoryRepository;
import org.ofz.repayment.RepaymentType;
import org.ofz.repayment.exception.repayment.InvalidPrepaymentAmountException;
import org.ofz.repayment.exception.repayment.NoRepaymentRecordsException;
import org.ofz.repayment.exception.repayment.TooHighPrepaymentAmountException;
import org.ofz.repayment.exception.user.UserNotFoundException;
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
    private final RepaymentHistoryRepository repaymentHistoryRepository;
    private final AccountUtils accountUtils;

    public PaymentInfoForCashResponse getPaymentInfo(Long userId) {

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        int thisMonthRepaymentAmount = payment.getPreviousMonthDebt();
        int creditLimit = payment.getCreditLimit();

        String accountNumber = payment.getRepaymentAccountNumber();
        int accountDeposit = getPaymentAccountData(accountNumber).getDeposit();

        Pageable pageable = PageRequest.of(0, 2);
        List<PaymentHistoriesResponse.PaymentHistoryDTO> currentPaymentHistories = paymentHistoryRepository.findPaymentHistoryByUserId(userId, pageable);

        return PaymentInfoForCashResponse.builder()
                .paymentAmount(thisMonthRepaymentAmount)
                .creditLimit(creditLimit)
                .accountDeposit(accountDeposit)
                .paymentHistories(currentPaymentHistories)
                .build();
    }

    public List<PaymentHistoriesResponse.PaymentHistoryDTO> getPaymentHistory(int month, Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저 정보가 조회되지 않습니다."));

        return paymentHistoryRepository.findPaymentHistoryByUserIdAndMonth(user.getId(), month);
    }

    public RepaymentAccountResponse getPaymentAccount(Long userId) {

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        String accountNumber = payment.getRepaymentAccountNumber();
        int totalDebt = payment.getPreviousMonthDebt() + payment.getCurrentMonthDebt();

        AccountResponse response = getPaymentAccountData(accountNumber);

        return RepaymentAccountResponse.builder()
                .totalDebt(totalDebt)
                .accountNumber(response.getAccountNumber())
                .companyCode(response.getCompanyCode())
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
    public CashRepaymentResponse prepayWithCash(CashPrepaymentRequest cashPrepaymentRequest) {

        Long userId = cashPrepaymentRequest.getUserId();
        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        int prepaymentAmount = cashPrepaymentRequest.getAmount();

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

        RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                .repaymentAmount(prepaymentAmount)
                .createdAt(LocalDateTime.now())
                .userId(userId)
                .type(RepaymentType.PRE_CASH)
                .build();

        repaymentHistoryRepository.save(repaymentHistory);
        paymentRepository.save(payment);

        return response;
    }

    private AccountResponse getPaymentAccountData(String accountNumber) {
        return accountUtils.fetchPaymentAccount(accountNumber);
    }
}
