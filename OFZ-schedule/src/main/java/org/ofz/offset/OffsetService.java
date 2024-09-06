package org.ofz.offset;

import org.ofz.management.projection.MortgagedStockProjection;
import org.ofz.management.repository.MortgagedStockRepository;
import org.ofz.management.repository.StockPriorityRepository;
import org.ofz.management.repository.StockRepository;
import org.ofz.offset.dto.*;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.repayment.RepaymentHistory;
import org.ofz.repayment.RepaymentHistoryRepository;
import org.ofz.repayment.RepaymentType;
import org.ofz.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OffsetService {
    private final RepaymentHistoryRepository repaymentHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final MortgagedStockRepository mortgagedStockRepository;
    private final StockPriorityRepository stockPriorityRepository;
    private final StockRepository stockRepository;
    private final WebClient webClient;

    @Value("${webclient.base-url}")
    private String partnersUrl;

    @Autowired
    public OffsetService(RepaymentHistoryRepository repaymentHistoryRepository, PaymentRepository paymentRepository, MortgagedStockRepository mortgagedStockRepository, StockPriorityRepository stockPriorityRepository, StockRepository stockRepository, WebClient.Builder webClientBuilder) {
        this.repaymentHistoryRepository = repaymentHistoryRepository;
        this.paymentRepository = paymentRepository;
        this.mortgagedStockRepository = mortgagedStockRepository;
        this.stockPriorityRepository = stockPriorityRepository;
        this.stockRepository = stockRepository;
        this.webClient = webClientBuilder.baseUrl(partnersUrl).build();
    }

    @Transactional
    @Scheduled(cron = "0 0 9 * * 1-5")
    public void processOffsets(){
        List<Payment> offsetTargets = paymentRepository.findByOverdueDay();
        for (Payment offsetTarget : offsetTargets) {
            processOffset(offsetTarget.getUser().getId());
        }
    }

    @Transactional
    public void processOffset(Long userId){
        PaymentOverdueDebtDto overdueAndDebt = getOverdueAndDebt(userId);
        int remainingDebt = getOverdueAndDebt(userId).getPreviousMonthDebt();

        List<MortgagedStockProjection> mortgagedStocks = getSortedMortgagedStock(userId);
        List<String> mortgagedStockCodes = getMortgagedStockCodes(mortgagedStocks);

        try {
            Payment payment = findPaymentByUserId(userId);
            CurrentStockPriceReq mortgagedStockCodesReq = new CurrentStockPriceReq(mortgagedStockCodes);
            CurrentStockPriceRes currentPriceOfMortgagedStocks = getCurrentStockRes(mortgagedStockCodesReq);

            int totalPayedDebt = 0;
            for (MortgagedStockProjection mortgagedStockProjection : mortgagedStocks) {
                EachCurrentStockDto currentStock = getCurrentStock(mortgagedStockProjection, currentPriceOfMortgagedStocks);

                String accountNumber = mortgagedStockProjection.getAccountNumber();
                String stockCode = mortgagedStockProjection.getStockCode();
                int quantity = mortgagedStockProjection.getQuantity();
                int price = currentStock.getAmount();

                int totalStockValue = price * quantity;
                int quantityToSell = totalStockValue <= remainingDebt ? quantity : (int) Math.ceil((double) remainingDebt / price);
                int payedDebt = sellStock(accountNumber, stockCode, quantityToSell);
                remainingDebt -= payedDebt;
                totalPayedDebt += payedDebt;

                updateStockTables(accountNumber, stockCode, quantityToSell, userId);
                payment.changeCreditLimit(0);

                // 한도가 0으로 바뀌었으니 재설정하라고 알림


                if(remainingDebt <= 0) {
                    int excessPayment = Math.abs(remainingDebt);
                    totalPayedDebt -= excessPayment;

                    renewPaymentIfAllPayed(payment, totalPayedDebt);
                    recordRepaymentHistory(payment, totalPayedDebt);
                    increaseDeposit(mortgagedStockProjection.getAccountNumber(), excessPayment);
                    break;
                }
            }

            if(remainingDebt > 0) {
                renewPaymentIfNotAllPayed(payment, totalPayedDebt);
                recordRepaymentHistory(payment, totalPayedDebt);
                // 채무 불이행 알림
            }
        } catch (WebClientResponseException e) {
            throw new RuntimeException("더미 서버 API 호출 실패: " + e.getMessage());
        }
    }

    private Payment findPaymentByUserId(Long userId){
        return paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다."));
    }

    private PaymentOverdueDebtDto getOverdueAndDebt(Long userId){
        Payment payment = findPaymentByUserId(userId);
        return new PaymentOverdueDebtDto(payment.getPreviousMonthDebt(), payment.getOverdueDay());
    }

    private List<MortgagedStockProjection> getSortedMortgagedStock(Long userId){
        List<MortgagedStockProjection> sortedPriorityMortgagedStocks = mortgagedStockRepository.sortPriorityMortgage(userId);
        List<MortgagedStockProjection> sortedNonPriorityMortgagedStocks = mortgagedStockRepository.sortNonPriorityMortgage(userId);

        List<MortgagedStockProjection> sortedMortgagedStocks = new ArrayList<>();
        sortedMortgagedStocks.addAll(sortedPriorityMortgagedStocks);
        sortedMortgagedStocks.addAll(sortedNonPriorityMortgagedStocks);

        return sortedMortgagedStocks;
    }

    private static List<String> getMortgagedStockCodes(List<MortgagedStockProjection> mortgagedStocks) {
        List<String> mortgagedStockCodes = new ArrayList<>();
        for (MortgagedStockProjection stock : mortgagedStocks) {
            mortgagedStockCodes.add(stock.getStockCode());
        }
        return mortgagedStockCodes;
    }

    private CurrentStockPriceRes getCurrentStockRes(CurrentStockPriceReq currentStockPriceReq) {
        if (currentStockPriceReq.getStockCodes().isEmpty()) {
            return new CurrentStockPriceRes(new ArrayList<>());
        }
        return Optional.ofNullable(
                webClient.post()
                        .uri(partnersUrl + "/securities/stocks/current")
                        .bodyValue(currentStockPriceReq)
                        .retrieve()
                        .bodyToMono(CurrentStockPriceRes.class)
                        .block()).orElseThrow(() -> new RuntimeException("실시간 주가를 가져올 수 없습니다."));
    }

    private static EachCurrentStockDto getCurrentStock(MortgagedStockProjection mortgagedStockProjection, CurrentStockPriceRes currentStockRes) {
        return currentStockRes.getCurrentStockList().stream()
                .filter(res -> res.getStockCode().equals(mortgagedStockProjection.getStockCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 종목의 주가 정보를 불러오지 못했습니다."));
    }

    private int sellStock(String accountNumber, String stockCode, int quantity){
        SellStockReq sellStockReq = new SellStockReq(accountNumber, quantity, stockCode);
        System.out.println("sellStockReq = " + sellStockReq);
        return Optional.ofNullable(
                webClient.put()
                        .uri(partnersUrl + "/securities/accounts/stocks")
                        .bodyValue(sellStockReq)
                        .retrieve()
                        .bodyToMono(SellStockRes.class)
                        .block()
        ).map(SellStockRes::getSellAmount).orElseThrow(() -> new RuntimeException("주식 매도 요청 실패"));
    }

    @Transactional
    private void updateStockTables(String accountNumber, String stockCode, int quantityToSell, Long userId) {
        stockPriorityRepository.reduceQuantity(accountNumber, stockCode, quantityToSell, userId);
        stockPriorityRepository.deleteIfQuantityZero(accountNumber, stockCode, userId);

        mortgagedStockRepository.reduceQuantity(accountNumber, stockCode, quantityToSell, userId);
        mortgagedStockRepository.deleteIfQuantityZero(accountNumber, stockCode, userId);

        stockRepository.reduceQuantity(accountNumber, stockCode, quantityToSell, userId);
        stockRepository.deleteIfQuantityZero(accountNumber, stockCode, userId);
    }

    private void increaseDeposit(String accountNumber, int value){
        IncreaseDepositReq increaseDepositReq = new IncreaseDepositReq(accountNumber, value);
        webClient.put()
                .uri(partnersUrl + "/mydata/accounts/deposit")
                .bodyValue(increaseDepositReq)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void renewPaymentIfNotAllPayed(Payment payment, int totalPayedDebt) {
        payment.decreasePreviousMonthDebt(totalPayedDebt);
        paymentRepository.save(payment);
    }

    private void renewPaymentIfAllPayed(Payment payment, int totalPayedDebt) {
        payment.enablePay();
        payment.resetOverdueDay();
        payment.decreasePreviousMonthDebt(totalPayedDebt);
        paymentRepository.save(payment);
    }

    private void recordRepaymentHistory(Payment payment, int totalPayedDebt) {
        RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                .userId(payment.getUser().getId())
                .repaymentAmount(totalPayedDebt)
                .type(RepaymentType.OFFSET)
                .createdAt(LocalDateTime.now())
                .build();
        repaymentHistoryRepository.save(repaymentHistory);
    }
}
