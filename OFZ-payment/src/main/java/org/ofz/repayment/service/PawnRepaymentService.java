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
import org.ofz.repayment.dto.MortgagedStockDTO;
import org.ofz.repayment.dto.PresentStockPriceDTO;
import org.ofz.repayment.dto.SellStockDTO;
import org.ofz.repayment.dto.request.PawnPrepaymentRequest;
import org.ofz.repayment.dto.request.PawnPrepaymentRequest.*;
import org.ofz.repayment.dto.request.RepaymentUserRequest;
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

    @Transactional
    public PaymentInfoForPawnResponse getPaymentInfo(RepaymentUserRequest repaymentUserRequest) {

        Long userId = repaymentUserRequest.getUserId();

        // 결제 가능 금액
        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보가 조회되지 않습니다."));

        int previousMonthDebt = payment.getPreviousMonthDebt();
        int currentMonthDebt = payment.getCurrentMonthDebt();
        int totalDebt = previousMonthDebt + currentMonthDebt;

        // 응답할 dto
        PaymentInfoForPawnResponse infoForPawnResponse = new PaymentInfoForPawnResponse(totalDebt);

        // 담보로 잡은 주식
        List<StockPriority> mortgagedStocks = stockPriorityRepository.findAllStockPrioritiesByUserId(userId);

        for (StockPriority stockPriority : mortgagedStocks) {

            // 주식 코드
            String stockCode = stockPriority.getStockCode();

            // 현재가
            PresentStockPriceDTO presentStockPrice = stockUtils.fetchPresentStockPrice(stockCode);

            StockInformation stockInformation = stockInformationRepository
                    .findByStockCode(stockCode)
                    .orElseThrow(() -> new StockInformationNotFoundException("존재하는 주식 코드가 아닙니다."));

            // TODO: 2024-09-04 전일 종가 이거 레디스에서 가져올 수 있는 걸로 알고 있음
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
    public PawnRepaymentResponse prepayWithPawn(PawnPrepaymentRequest pawnPrepaymentRequest) {

        Long userId = pawnPrepaymentRequest.getUserId();
        int repaymentAmount = pawnPrepaymentRequest.getRepaymentAmount();

        if (repaymentAmount <= 0) {
            throw new InvalidPrepaymentAmountException("상환할 금액을 다시 설정해주세요.");
        }

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 테이블 없음"));

        List<SelectedStock> selectedStocks = pawnPrepaymentRequest.getSelectedStocks();

        //  더미에 날릴 데이터
        List<SellStockDTO> sellStocks = new ArrayList<>();

        int sum = 0;

        for (SelectedStock selectedStock : selectedStocks) {

            int stockRank = selectedStock.getStockRank();
            int deductionQuantity = selectedStock.getQuantity();
            String accountNumber = selectedStock.getAccountNumber();
            String stockCode = selectedStock.getStockCode();

            // 우선 순위 스톡
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

            // TODO: 2024-09-04 같을 때, 남을 때 ->
            //  우선 순위 스톡, 모기지 스톡,
            //  그냥 스톡, 더미 서버 스톡에 다 변경 사항 적용해야 함

            // TODO: 2024-09-04 !!!!!!!!!!!!!!!!! 담보 선결제를 하면 한도&담보 설정을 다시 해야함!
            //  그렇다면 최종적으로 결제가 잘 이루어지면 [우선순위 스톡, 모기지 스톡]을 날려줘야 한다!

            // 우선
            int currentStockPriorityQuantity = stockPriority.getQuantity();
            if (currentStockPriorityQuantity - deductionQuantity < 0) {
                throw new TooHighPawnStockQuantityException("차감하려는 수량이 현재 잡혀 있는 주식 수량보다 높습니다.");
            }

            // 담
            int currentMortgagedStockQuantity = mortgagedStock.getQuantity();
            if (currentMortgagedStockQuantity - deductionQuantity < 0) {
                throw new TooHighPawnStockQuantityException("우선 순위 스톡 수량 빼고 여기까지 왔는데, 담보 스톡에서 차감하려는 수량이 높다고 에러가 나왔네?");
            }

            // 걍주
            int currentStockQuantity = stock.getQuantity();
            if (currentStockQuantity - deductionQuantity < 0) {
                throw new TooHighPawnStockQuantityException("우선 순위, 담보 다 통과했는데 스톡에서 걸려버리네?");
            }
            stock.minusQuantity(deductionQuantity);
            if (stock.getQuantity() == 0) {
                stockRepository.delete(stock);
            } else {
                stockRepository.save(stock);
            }

            // 더미
            sellStocks.add(SellStockDTO.builder()
                    .quantity(deductionQuantity)
                    .accountNumber(accountNumber)
                    .stockCode(stockCode)
                    .build());
        }

        stockPriorityRepository.deleteAllByUserId(userId);
        mortgagedStockRepository.deleteAllByUserId(userId);

        for (SellStockDTO sellStock : sellStocks) {
            // 더미
            SellAmountDTO sellAmount = stockUtils.fetchRequestSellStocks(sellStock);

            sum += sellAmount.getSellAmount();
        }

        // TODO: 2024-09-04 선결제 계산
        int previousMonthDebt = payment.getPreviousMonthDebt();
        int currentMonthDebt = payment.getCurrentMonthDebt();
        int totalDebt = previousMonthDebt + currentMonthDebt;

        // 만약 돈 깠는데 남으면
        int amountToAccount = 0;
        if (sum - totalDebt > 0) {

            amountToAccount = sum - totalDebt;

            String userAccountNumber = payment.getRepaymentAccountNumber();

            payment.minusPreviousMonthDebt(previousMonthDebt);
            payment.minusCurrentMonthDebt(currentMonthDebt);

            accountUtils.fetchDepositToAccount(userAccountNumber, amountToAccount);
        }
        // 안 남았으면
        else {
            payment.deductRepaymentAmount(sum);
        }

        payment.disablePay();

        paymentRepository.save(payment);

        return PawnRepaymentResponse.builder()
                .repaymentAmount(repaymentAmount)
                .realRepaymentAmount(sum)
                .amountToAccount(amountToAccount)
                .message("일단 됐다")
                .build();
    }
}
