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
import org.ofz.management.utils.StockStability;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.payment.exception.payment.PaymentNotFoundException;
import org.ofz.redis.RedisUtil;
import org.ofz.repayment.RepaymentHistory;
import org.ofz.repayment.RepaymentHistoryRepository;
import org.ofz.repayment.RepaymentType;
import org.ofz.repayment.dto.MortgagedStockDTO;
import org.ofz.repayment.dto.PresentStockPriceDTO;
import org.ofz.repayment.dto.SellStockDTO;
import org.ofz.repayment.dto.projection.QuantityAndStockCodeOfMortgagedStock;
import org.ofz.repayment.dto.request.PawnPrepaymentRequest;
import org.ofz.repayment.dto.request.PawnPrepaymentRequest.*;
import org.ofz.repayment.dto.response.PawnRepaymentResponse;
import org.ofz.repayment.dto.response.PaymentInfoForPawnResponse;
import org.ofz.repayment.exception.repayment.*;
import org.ofz.repayment.service.utils.DateChecker;
import org.ofz.repayment.utils.AccountUtils;
import org.ofz.repayment.utils.StockUtils;
import org.ofz.repayment.utils.StockUtils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
    private final RepaymentHistoryRepository repaymentHistoryRepository;

    public PaymentInfoForPawnResponse getPaymentInfo(Long userId) {

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보가 조회되지 않습니다."));

        int previousMonthDebt = payment.getPreviousMonthDebt();
        int currentMonthDebt = payment.getCurrentMonthDebt();
        int totalDebt = previousMonthDebt + currentMonthDebt;

        PaymentInfoForPawnResponse infoForPawnResponse = new PaymentInfoForPawnResponse(totalDebt);

        List<StockPriority> stockPriorities = stockPriorityRepository.findStockPrioritiesByUserIdOrderByStockRank(userId);
        List<MortgagedStock> mortgagedStocks = mortgagedStockRepository.findMortgagedStocksByUserIdOrderByStockCode(userId);
        Map<String, Integer> existStockPriority = new HashMap<>();
        Map<String, Integer> existPresentStockPrice = new HashMap<>();
        for (StockPriority stockPriority : stockPriorities) {

            String stockCode = stockPriority.getStockCode();
            int quantity = stockPriority.getQuantity();

            existStockPriority.put(stockCode, existStockPriority.getOrDefault(stockCode, 0) + quantity);

            PresentStockPriceDTO presentStockPrice = stockUtils.fetchPresentStockPrice(stockCode);

            int presentStockAmount = presentStockPrice.getAmount();
            if (!existPresentStockPrice.containsKey(stockCode)) {
                existPresentStockPrice.put(stockCode, presentStockAmount);
            }

            StockInformation stockInformation = stockInformationRepository
                    .findByStockCode(stockCode)
                    .orElseThrow(() -> new StockInformationNotFoundException("존재하는 주식 코드가 아닙니다."));

            Integer previousPrice = redisUtil.fetchStoredPreviousPrice(stockCode);
            if (previousPrice == null) {
                previousPrice = stockUtils.fetchPreviousStockPrice(stockCode);
            }

            infoForPawnResponse.addMortgagedStock(
                    MortgagedStockDTO.fromStockPriority(
                            stockPriority,
                            stockInformation,
                            previousPrice,
                            presentStockAmount
                    )
            );
        }

        for (MortgagedStock mortgagedStock : mortgagedStocks) {

            String stockCode = mortgagedStock.getStockCode();
            int mortgagedStockQuantity = mortgagedStock.getQuantity();

            int excludedMortgagedStockQuantity = existStockPriority.getOrDefault(stockCode, 0);

            mortgagedStock.minusQuantity(excludedMortgagedStockQuantity);

            StockInformation stockInformation = stockInformationRepository
                    .findByStockCode(stockCode)
                    .orElseThrow(() -> new StockInformationNotFoundException("존재하는 주식 코드가 아닙니다."));

            Integer previousPrice = redisUtil.fetchStoredPreviousPrice(stockCode);
            if (previousPrice == null) {
                previousPrice = stockUtils.fetchPreviousStockPrice(stockCode);
            }

            if (mortgagedStockQuantity != excludedMortgagedStockQuantity) {

                infoForPawnResponse.addMortgagedStock(
                        MortgagedStockDTO.fromMortgagedStock(
                                mortgagedStock,
                                stockInformation,
                                previousPrice,
                                existPresentStockPrice.getOrDefault(stockCode, stockUtils.fetchPresentStockPrice(stockCode).getAmount())
                        )
                );
            }
        }

        return infoForPawnResponse;
    }

    @Transactional
    public PawnRepaymentResponse prepayWithPawn(Long userId, PawnPrepaymentRequest pawnPrepaymentRequest) {

//        if (DateChecker.isWeekend()) {
//            throw new ClosedDaysException("금일은 휴장일입니다.");
//        }

        // 결제할 선정된 금액
        int repaymentAmount = pawnPrepaymentRequest.getRepaymentAmount();

        if (repaymentAmount < 0) {
            throw new InvalidPrepaymentAmountException("상환할 금액을 다시 설정해주세요.");
        }

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보가 조회되지 않습니다."));

        int totalDebt = payment.getTotalDebt();

        if (repaymentAmount > totalDebt) {
            throw new InvalidPrepaymentAmountException("결제 가능 금액보다 높은 금액으로 설정할 수 없습니다.");
        }

        List<SelectedStock> selectedStocks = pawnPrepaymentRequest.getSelectedStocks();

        List<SellStockDTO> sellStocks = new ArrayList<>();

        int sum = 0;

        // TODO: 2024-09-06 삼성전자를 다 팔아서 데이터가 잘 날아가는지 확인해 보자 

        for (SelectedStock selectedStock : selectedStocks) {

            int stockRank = selectedStock.getStockRank();
            int deductionQuantity = selectedStock.getQuantity();
            String accountNumber = selectedStock.getAccountNumber();
            String stockCode = selectedStock.getStockCode();

            if (deductionQuantity <= 0) {
                throw new InvalidPrepaymentAmountException("0 이하로 수량을 설정할 수 없습니다.");
            }

            Stock stock = stockRepository
                    .findStockByAccountNumberAndStockCode(accountNumber, stockCode)
                    .orElseThrow(() -> new StockNotFoundException("계좌 번호와 주식 코드로 증권이 조회되지 않습니다."));

            // 우선 순위가 아닌 것
            if (stockRank == 0) {
                MortgagedStock mortgagedStock = mortgagedStockRepository
                        .findMortgagedStockByAccountNumberAndStockCodeAndUserId(accountNumber, stockCode, userId)
                        .orElseThrow(() -> new MortgagedNotFoundException("계좌 번호화 주식 코드로 담보 증권이 조회되지 않습니다."));

                int priorityMortgagedStockQuantity = stockPriorityRepository
                        .findStockPriorityQuantityByUserIdAndAccountNumberAndStockCode(userId, accountNumber, stockCode)
                        .orElse(0);

                int mortgagedStockQuantity = mortgagedStock.getQuantity();
                checkMortgagedDiffPriority(mortgagedStockQuantity, priorityMortgagedStockQuantity, deductionQuantity);
                mortgagedStock.minusQuantity(deductionQuantity);
                // 저장 -> 우선 순위 담보가 없는데 0개가 되면 레코드 삭제

                if (mortgagedStock.getQuantity() == 0) {
                    mortgagedStockRepository.delete(mortgagedStock);
                } else {
                    mortgagedStockRepository.save(mortgagedStock);
                }

                int stockQuantity = stock.getQuantity();
                checkStockDiffMortgage(stockQuantity, deductionQuantity);
                stock.minusQuantity(deductionQuantity);
                // 저장 -> 0개가 되면 레코드 삭제

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

                continue;
            }

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
                    .findMortgagedStockByAccountNumberAndStockCodeAndUserId(accountNumber, stockCode, userId)
                    .orElseThrow(() -> new MortgagedNotFoundException("계좌 번호화 주식 코드로 담보 증권이 조회되지 않습니다."));

            // 우선 순위
            int currentStockPriorityQuantity = stockPriority.getQuantity();
            if (currentStockPriorityQuantity - deductionQuantity < 0) {
                throw new TooHighPawnStockQuantityException("차감하려는 수량이 현재 잡혀 있는 주식 수량보다 높습니다. (우선 순위 주식): " + stockCode);
            }
            stockPriority.minusQuantity(deductionQuantity);
            if (stockPriority.getQuantity() == 0) {
                stockPriorityRepository.delete(stockPriority);
            } else {
                stockPriorityRepository.save(stockPriority);
            }

            // 모기지
            int currentMortgagedStockQuantity = mortgagedStock.getQuantity();
            if (currentMortgagedStockQuantity - deductionQuantity < 0) {
                throw new TooHighPawnStockQuantityException("차감하려는 수량이 현재 잡혀 있는 주식 수량보다 높습니다. (담보로 잡은 주식)");
            }
            mortgagedStock.minusQuantity(deductionQuantity);
            if (mortgagedStock.getQuantity() == 0) {
                mortgagedStockRepository.delete(mortgagedStock);
            } else {
                mortgagedStockRepository.save(mortgagedStock);
            }

            // 그냥 스톡
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

        boolean hasStockPriority = stockPriorityRepository.existsByUserId(userId);
        boolean hasMortgagedStock = mortgagedStockRepository.existsByUserId(userId);

        if (!hasMortgagedStock && hasStockPriority) {
            throw new InconsistentRecordException("담보 주식과 우선 순위를 잡은 담보 데이터 간에 불일치가 생겼습니다.");
        }

        for (SellStockDTO sellStock : sellStocks) {

            SellAmountDTO sellAmount = stockUtils.fetchRequestSellStocks(sellStock);

            sum += sellAmount.getSellAmount();
        }

        int amountToAccount = 0;
        int realRepaymentAmount;

        if (sum > repaymentAmount) {
            amountToAccount = sum - repaymentAmount;

            payment.deductRepaymentAmount(repaymentAmount);

            String userAccountNumber = payment.getRepaymentAccountNumber();

            accountUtils.fetchDepositToAccount(userAccountNumber, amountToAccount);
            realRepaymentAmount = repaymentAmount;
        } else {

            payment.deductRepaymentAmount(sum);
            realRepaymentAmount = sum;
        }

        // TODO: 2024-09-06 담보 유지 비율 계산 로직이 필요
        //  담보 비율 로직이 깨지지 않았다면 payFlag 는 false 로 바꿔 주지 않음
        //  깨진다면 한도를 0으로 바꿔 두고 플래그를 false 로 바꿔야 함

        boolean marginRequirement = true; // true 이면 비율이 깨지지 않았다는 것!
        int userCreditLimit = payment.getCreditLimit();
        int finalTotalDebt = payment.getTotalDebt();

        if (hasMortgagedStock) {
            marginRequirement = checkMarginRequirement(userCreditLimit, userId);
        } else {
            payment.changeCreditLimit(0);
        }

        if (!marginRequirement || (!hasMortgagedStock && finalTotalDebt > 0) || payment.getCreditLimit() == 0) {
            payment.breakRate();
            payment.disablePay();
        }

        RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                .repaymentAmount(realRepaymentAmount)
                .createdAt(LocalDateTime.now())
                .userId(userId)
                .type(RepaymentType.PRE_PAWN)
                .build();

        repaymentHistoryRepository.save(repaymentHistory);
        paymentRepository.save(payment);

        return PawnRepaymentResponse.builder()
                .repaymentAmount(repaymentAmount)
                .totalSellAmount(sum)
                .realRepaymentAmount(realRepaymentAmount)
                .amountToAccount(amountToAccount)
                .message("담보 선결제가 완료됐습니다.")
                .build();
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

    private void checkStockDiffMortgage(int stockQuantity, int deductionQuantity) {

        int diffQuantity = stockQuantity - deductionQuantity;

        if (diffQuantity < 0) {
            throw new TooHighPawnStockQuantityException("특정 증권 보유 개수가 차감하려는 값보다 작습니다.");
        }
    }

    private void checkMortgagedDiffPriority(int mortgagedStockQuantity, int priorityMortgagedStockQuantity, int deductionQuantity) {

        int diffQuantity = mortgagedStockQuantity - priorityMortgagedStockQuantity;

        if (diffQuantity - deductionQuantity < 0) {
            throw new TooHighPawnStockQuantityException("항목에 대한 제한 수량을 초과하였습니다.");
        }
    }
}
