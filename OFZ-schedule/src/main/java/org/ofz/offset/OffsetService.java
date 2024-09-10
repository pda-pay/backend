package org.ofz.offset;

import org.ofz.management.projection.MortgagedStockProjection;
import org.ofz.management.repository.MortgagedStockRepository;
import org.ofz.management.repository.StockPriorityRepository;
import org.ofz.management.repository.StockRepository;
import org.ofz.offset.dto.*;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.rabbitMQ.NotificationPage;
import org.ofz.rabbitMQ.Publisher;
import org.ofz.rabbitMQ.rabbitDto.AllPayedOffsetLogDto;
import org.ofz.rabbitMQ.rabbitDto.NotAllPayedOffsetLogDto;
import org.ofz.rabbitMQ.rabbitDto.NotificationMessage;
import org.ofz.rabbitMQ.rabbitDto.RepaymentHistoryLogDTO;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.ofz.rabbitMQ.NotificationType.*;

@Service
public class OffsetService {
    private final RepaymentHistoryRepository repaymentHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final MortgagedStockRepository mortgagedStockRepository;
    private final StockPriorityRepository stockPriorityRepository;
    private final StockRepository stockRepository;
    private final WebClient webClient;
    private final Publisher<NotificationMessage> notificationpublisher;
    private final Publisher<RepaymentHistoryLogDTO> publisher;
    private final Publisher<AllPayedOffsetLogDto> allPayedAdminPublisher;
    private final Publisher<NotAllPayedOffsetLogDto> notAllPayedAdminPublisher;

    @Value("${webclient.base-url}")
    private String partnersUrl;

    @Autowired
    public OffsetService(RepaymentHistoryRepository repaymentHistoryRepository, PaymentRepository paymentRepository, MortgagedStockRepository mortgagedStockRepository, StockPriorityRepository stockPriorityRepository, StockRepository stockRepository, WebClient.Builder webClientBuilder, Publisher<NotificationMessage> notificationpublisher, Publisher<AllPayedOffsetLogDto> allPayedAdminPublisher, Publisher<NotAllPayedOffsetLogDto> notAllPayedAdminPublisher) {
        this.repaymentHistoryRepository = repaymentHistoryRepository;
        this.paymentRepository = paymentRepository;
        this.mortgagedStockRepository = mortgagedStockRepository;
        this.stockPriorityRepository = stockPriorityRepository;
        this.stockRepository = stockRepository;
        this.webClient = webClientBuilder.baseUrl(partnersUrl).build();
        this.notificationpublisher = notificationpublisher;
        this.allPayedAdminPublisher = allPayedAdminPublisher;
        this.notAllPayedAdminPublisher = notAllPayedAdminPublisher;
    }

    @Transactional
    @Scheduled(cron = "0 52 15 * * 1-5")
    public void processOffsets(){
        List<Payment> offsetTargets = paymentRepository.findByOverdueDay();
        for (Payment offsetTarget : offsetTargets) {
            processOffset(offsetTarget.getUser());
        }
    }

    @Transactional
    public void processOffset(User user){
        Long userId = user.getId();
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

                if(remainingDebt <= 0) { // 반대매매로 전부 상환한 사용자
                    int excessPayment = Math.abs(remainingDebt);
                    totalPayedDebt -= excessPayment;

                    renewPaymentIfAllPayed(payment, totalPayedDebt);
                    recordRepaymentHistory(payment, totalPayedDebt);
                    increaseDeposit(mortgagedStockProjection.getAccountNumber(), excessPayment);

                    // 사용자에게 알림
                    notifyToAllPayedUser(user, excessPayment);
                    break;
                }
            }

            if(remainingDebt > 0) { // 반대매매로도 모두 상환하지 못한 사용자
                renewPaymentIfNotAllPayed(payment, totalPayedDebt);
                recordRepaymentHistory(payment, totalPayedDebt);

                // 사용자에게 알림
                notifyToNotAllPayedUser(user);
            }
            // 관리자에게 메시지
            sendMessageToAdmin(user, payment, totalPayedDebt, userId);
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

    private void notifyToAllPayedUser(User user, int excessPayment) {
        NotificationMessage notificationAllPayedRepaymentMessage = NotificationMessage.builder()
                .loginId(user.getLoginId())
                .title("반대매매로 채무가 모두 상환되었습니다.")
                .body("반대매매가 일어나서 채무가 모두 상환되었습니다." +
                        (excessPayment > 0 ? "\n매도 후 " + excessPayment + "만큼의 금액이 남아 해당 증권을 보유한 계좌로 입금되었습니다." : ""))
                .category(상환)
                .page(NotificationPage.ASSET)
                .build();
        notificationpublisher.sendMessage(notificationAllPayedRepaymentMessage);

        NotificationMessage notificationAllPayedMortgageChangeMessage = NotificationMessage.builder()
                .loginId(user.getLoginId())
                .title("반대매매로 인해 보유 주식 및 담보 주식이 변경되었습니다.")
                .body("반대매매로 인해 담보 주식이 매도되었습니다. 보유 주식 및 담보 주식이 변경되었으니 확인해주세요.")
                .category(담보)
                .page(NotificationPage.ASSET)
                .build();
        notificationpublisher.sendMessage(notificationAllPayedMortgageChangeMessage);

        NotificationMessage notificationAllPayedLimitChangeMessage = NotificationMessage.builder()
                .loginId(user.getLoginId())
                .title("반대매매로 인해 한도가 0으로 변경되었습니다.")
                .body("반대매매가 일어나서 한도가 0으로 변경되었습니다. 결제 서비스를 이용하려면 재설정해주세요.")
                .category(한도)
                .page(NotificationPage.ASSET)
                .build();
        notificationpublisher.sendMessage(notificationAllPayedLimitChangeMessage);
    }

    private void notifyToNotAllPayedUser(User user) {
        NotificationMessage notificationNotAllPayedRepaymentMessage = NotificationMessage.builder()
                .loginId(user.getLoginId())
                .title("반대매매가 일어났지만 채무가 모두 상환되지 못했습니다.")
                .body("반대매매가 일어나서 채무가 일부 상환되었지만 담보가 부족하여 모두 상환되지 못했어요. 담보를 더 잡거나 선결제를 진행해주세요.")
                .category(상환)
                .page(NotificationPage.ASSET)
                .build();
        notificationpublisher.sendMessage(notificationNotAllPayedRepaymentMessage);

        NotificationMessage notificationNotAllPayedMortgageChangeMessage = NotificationMessage.builder()
                .loginId(user.getLoginId())
                .title("반대매매로 인해 보유 주식 및 담보 주식이 변경되었습니다.")
                .body("반대매매로 인해 담보 주식이 매도되었습니다. 보유 주식 및 담보 주식이 변경되었으니 확인해주세요.")
                .category(담보)
                .page(NotificationPage.ASSET)
                .build();
        notificationpublisher.sendMessage(notificationNotAllPayedMortgageChangeMessage);

        NotificationMessage notificationAllPayedLimitChangeMessage = NotificationMessage.builder()
                .loginId(user.getLoginId())
                .title("반대매매로 인해 한도가 0으로 변경되었습니다.")
                .body("반대매매가 일어나서 한도가 0으로 변경되었습니다. 담보를 재설정하고 한도를 설정해주세요.")
                .category(한도)
                .page(NotificationPage.ASSET)
                .build();
        notificationpublisher.sendMessage(notificationNotAllPayedMortgageChangeMessage);
    }

    private void sendMessageToAdmin(User user, Payment payment, int totalPayedDebt, Long userId) {
        String bankAccount = payment.getRepaymentAccountNumber();
        LocalDateTime nowTime = LocalDateTime.now();
        RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                .repaymentAmount(totalPayedDebt)
                .createdAt(nowTime)
                .userId(userId)
                .type(RepaymentType.OFFSET)
                .build();

        RepaymentHistory savedrepaymentHistory = repaymentHistoryRepository.save(repaymentHistory);

        RepaymentHistoryLogDTO log = RepaymentHistoryLogDTO.builder()
                .id(savedrepaymentHistory.getId())
                .loginId(user.getLoginId())
                .amount(totalPayedDebt)
                .accountNumber(bankAccount)
                .type(RepaymentType.OFFSET.kor)
                .date(nowTime)
                .build();
        publisher.sendMessage(log);
    }
}

