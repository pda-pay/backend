package org.ofz.repayment.service;

import lombok.RequiredArgsConstructor;
import org.ofz.management.MortgagedStock;
import org.ofz.management.StockInformation;
import org.ofz.management.StockPriority;
import org.ofz.management.entity.Stock;
import org.ofz.management.exception.StockInformationNotFoundException;
import org.ofz.management.repository.MortgagedStockRepository;
import org.ofz.management.StockInformationRepository;
import org.ofz.management.repository.StockPriorityRepository;
import org.ofz.management.repository.StockRepository;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.payment.exception.payment.PaymentNotFoundException;
import org.ofz.redis.RedisUtil;
import org.ofz.repayment.dto.MortgagedStockDTO;
import org.ofz.repayment.dto.PresentStockPriceDTO;
import org.ofz.repayment.dto.SellStockDTO;
import org.ofz.repayment.dto.request.PawnPrepaymentRequest;
import org.ofz.repayment.dto.request.PawnPrepaymentRequest.*;
import org.ofz.repayment.dto.response.PawnRepaymentResponse;
import org.ofz.repayment.dto.response.PaymentInfoForPawnResponse;
import org.ofz.repayment.exception.repayment.*;
import org.ofz.repayment.utils.AccountUtils;
import org.ofz.repayment.utils.StockUtils;
import org.ofz.repayment.utils.StockUtils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PawnRepaymentService {

    private final PaymentRepository paymentRepository;
    private final StockInformationRepository stockInformationRepository;
    private final StockPriorityRepository stockPriorityRepository;
    private final MortgagedStockRepository mortgagedStockRepository;
    private final StockRepository stockRepository;
    private final StockUtils stockUtils;
    private final AccountUtils accountUtils;
    private final RedisUtil redisUtil;

    @Transactional
    public PaymentInfoForPawnResponse getPaymentInfo(Long userId) {

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보가 조회되지 않습니다."));

        int previousMonthDebt = payment.getPreviousMonthDebt();
        int currentMonthDebt = payment.getCurrentMonthDebt();
        int totalDebt = previousMonthDebt + currentMonthDebt;

        PaymentInfoForPawnResponse infoForPawnResponse = new PaymentInfoForPawnResponse(totalDebt);

        List<StockPriority> mortgagedStocks = stockPriorityRepository.findAllStockPrioritiesByUserId(userId);

        for (StockPriority stockPriority : mortgagedStocks) {

            String stockCode = stockPriority.getStockCode();

            PresentStockPriceDTO presentStockPrice = stockUtils.fetchPresentStockPrice(stockCode);

            StockInformation stockInformation = stockInformationRepository
                    .findByStockCode(stockCode)
                    .orElseThrow(() -> new StockInformationNotFoundException("존재하는 주식 코드가 아닙니다."));

            // TODO: 2024-09-04 전일 종가 이거 레디스에서 가져올 수 있는 걸로 알고 있음
            //  int prevPrice = redisUtil.fetchStoredPreviousPrice(stockCode);
            int previousPrice = stockUtils.fetchPreviousStockPrice(stockCode);

            infoForPawnResponse.addMortgagedStock(
                    MortgagedStockDTO.builder()
                            .stockPriority(stockPriority)
                            .stockInformation(stockInformation)
                            .previousPrice(previousPrice)
                            .presentValue(presentStockPrice.getAmount())
                            .build()
            );
        }

        return infoForPawnResponse;
    }

    @Transactional
    public PawnRepaymentResponse prepayWithPawn(Long userId, PawnPrepaymentRequest pawnPrepaymentRequest) {

        int repaymentAmount = pawnPrepaymentRequest.getRepaymentAmount();

        if (repaymentAmount <= 0) {
            throw new InvalidPrepaymentAmountException("상환할 금액을 다시 설정해주세요.");
        }

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보가 조회되지 않습니다."));

        List<SelectedStock> selectedStocks = pawnPrepaymentRequest.getSelectedStocks();

        List<SellStockDTO> sellStocks = new ArrayList<>();

        int sum = 0;

        for (SelectedStock selectedStock : selectedStocks) {

            int stockRank = selectedStock.getStockRank();
            int deductionQuantity = selectedStock.getQuantity();
            String accountNumber = selectedStock.getAccountNumber();
            String stockCode = selectedStock.getStockCode();

            StockPriority stockPriority = stockPriorityRepository
                    .findStockPriorityByStockRankAndAccountNumberAndStockCodeAndUserId(
                            stockRank,
                            accountNumber,
                            stockCode,
                            userId
                    )
                    .orElseThrow(() -> new StockPriorityNotFoundException("우선 순위로 잡은 주식이 조회되지 않습니다."));

            // 모기지 스톡
            MortgagedStock mortgagedStock = mortgagedStockRepository
                    .findMortgagedStockByAccountNumberAndStockCode(accountNumber, stockCode)
                    .orElseThrow(() -> new MortgagedNotFoundException("계좌 번호화 주식 코드로 담보 증권이 조회되지 않습니다."));

            // 그냥 스톡
            Stock stock = stockRepository
                    .findStockByAccountNumberAndStockCode(accountNumber, stockCode)
                    .orElseThrow(() -> new StockNotFoundException("계좌 번호와 주식 코드로 증권이 조회되지 않습니다."));

            int currentStockPriorityQuantity = stockPriority.getQuantity();
            if (currentStockPriorityQuantity - deductionQuantity < 0) {
                throw new TooHighPawnStockQuantityException("차감하려는 수량이 현재 잡혀 있는 주식 수량보다 높습니다. (우선 순위 주식)");
            }

            int currentMortgagedStockQuantity = mortgagedStock.getQuantity();
            if (currentMortgagedStockQuantity - deductionQuantity < 0) {
                throw new TooHighPawnStockQuantityException("차감하려는 수량이 현재 잡혀 있는 주식 수량보다 높습니다. (담보로 잡은 주식)");
            }

            int currentStockQuantity = stock.getQuantity();
            if (currentStockQuantity - deductionQuantity < 0) {
                throw new TooHighPawnStockQuantityException("차감하려는 수량이 현재 잡혀 있는 주식 수량보다 높습니다. (보유 주식)");
            }
            stock.minusQuantity(deductionQuantity);
            if (stock.getQuantity() == 0) {
                stockRepository.delete(stock);
            } else {
                stockRepository.save(stock);
            }

            sellStocks.add(SellStockDTO.builder()
                    .quantity(deductionQuantity)
                    .accountNumber(accountNumber)
                    .stockCode(stockCode)
                    .build());
        }

        stockPriorityRepository.deleteAllByUserId(userId);
        mortgagedStockRepository.deleteAllByUserId(userId);

        for (SellStockDTO sellStock : sellStocks) {

            SellAmountDTO sellAmount = stockUtils.fetchRequestSellStocks(sellStock);

            sum += sellAmount.getSellAmount();
        }

        int previousMonthDebt = payment.getPreviousMonthDebt();
        int currentMonthDebt = payment.getCurrentMonthDebt();
        int totalDebt = previousMonthDebt + currentMonthDebt;

        int amountToAccount = 0;
        if (sum - totalDebt > 0) {

            amountToAccount = sum - totalDebt;

            String userAccountNumber = payment.getRepaymentAccountNumber();

            payment.minusPreviousMonthDebt(previousMonthDebt);
            payment.minusCurrentMonthDebt(currentMonthDebt);

            accountUtils.fetchDepositToAccount(userAccountNumber, amountToAccount);
        } else {
            payment.deductRepaymentAmount(sum);
        }

        payment.disablePay();

        paymentRepository.save(payment);

        return PawnRepaymentResponse.builder()
                .repaymentAmount(repaymentAmount)
                .realRepaymentAmount(sum)
                .amountToAccount(amountToAccount)
                .message("담보 선결제가 완료됐습니다.")
                .build();
    }
}
