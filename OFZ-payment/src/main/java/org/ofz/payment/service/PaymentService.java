package org.ofz.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.ofz.payment.config.WebSocketHandler;
import org.ofz.payment.dto.PaymentResponse;
import org.ofz.payment.dto.PaymentHistoryDTO;
import org.ofz.payment.dto.PaymentRequest;
import org.ofz.payment.entity.Franchise;
import org.ofz.payment.entity.Payment;
import org.ofz.payment.entity.PaymentHistory;
import org.ofz.payment.exception.ExceededCreditLimitException;
import org.ofz.payment.exception.websocket.ConvertMessageToJsonException;
import org.ofz.payment.exception.websocket.InvalidWebSocketSessionException;
import org.ofz.payment.exception.websocket.WebSocketSessionNotFoundException;
import org.ofz.payment.exception.websocket.PaymentNotFoundException;
import org.ofz.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final WebSocketHandler webSocketHandler;
    private final PaymentRepository paymentRepository;
    private final FranchiseService franchiseService;
    private final PaymentHistoryService paymentHistoryService;

    @Transactional
    public PaymentResponse payment(PaymentRequest paymentRequest) throws IOException {

        WebSocketSession session = webSocketHandler.getSession(paymentRequest.getTransactionId());

        validationSession(session);

        // 결제 정보
        Payment payment = paymentRepository
                .findPaymentByUserId(paymentRequest.getUserId())
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        // 가맹점 정보
        Franchise franchise = franchiseService.getFranchise(paymentRequest.getFranchiseCode());

        // 결제 가능?
        int creditLimit = payment.getCreditLimit();
        int previousMonthDebt = payment.getPreviousMonthDebt();
        int currentMonthDebt = payment.getCurrentMonthDebt();
        int leftCreditLimit = creditLimit - (currentMonthDebt);

        if (creditLimit < currentMonthDebt + paymentRequest.getPaymentAmount()) {
            throw new ExceededCreditLimitException(franchise.getName(), paymentRequest.getPaymentAmount(), "한도 초과");
        }

        payment.plusCurrentMonthDebt(paymentRequest.getPaymentAmount());

        // 결제 내역
        PaymentHistoryDTO paymentHistoryDTO = PaymentHistoryDTO.builder()
                .userId(payment.getUserId())
                .paymentAmount(paymentRequest.getPaymentAmount())
                .franchise(franchise)
                .build();
        PaymentHistory paymentHistory = paymentHistoryDTO.toEntity();

        // 저장
        paymentRepository.save(payment);
        paymentHistoryService.savePaymentHistory(paymentHistory);

        // 응답
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .franchiseName(franchise.getName())
                .paymentAmount(paymentRequest.getPaymentAmount())
                .date(LocalDateTime.now())
                .leftCreditLimit(leftCreditLimit)
                .message("결제가 완료되었습니다.")
                .build();

        String message = convertMessageToJson(paymentResponse);

        session.sendMessage(new TextMessage(message));
        webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        return paymentResponse;
    }

    private String convertMessageToJson(PaymentResponse paymentResponse) throws ConvertMessageToJsonException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            return objectMapper.writeValueAsString(paymentResponse);
        } catch (JsonProcessingException e) {
            throw new ConvertMessageToJsonException("응답 메시지 직혈화 실패", e);
        }
    }

    private void validationSession(WebSocketSession session) {

        if (session == null) {
            throw new WebSocketSessionNotFoundException("결제 실패. 웹소켓을 찾을 수 없습니다.");
        }

        if (!session.isOpen()) {
            throw new InvalidWebSocketSessionException("결제 실패. 웹소켓이 유효하지 않습니다.");
        }
    }
}
