package org.ofz.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.ofz.payment.config.WebSocketHandler;
import org.ofz.payment.dto.*;
import org.ofz.payment.entity.Franchise;
import org.ofz.payment.entity.Payment;
import org.ofz.payment.entity.PaymentHistory;
import org.ofz.payment.exception.payment.ExceededCreditLimitException;
import org.ofz.payment.exception.payment.PaymentNotFoundException;
import org.ofz.payment.exception.payment.PaymentPasswordMismatchException;
import org.ofz.payment.exception.websocket.*;
import org.ofz.payment.repository.PaymentRepository;
import org.ofz.payment.utils.PaymentTokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final PaymentTokenUtils paymentTokenUtils;

    @Transactional
    public PaymentResponse payment(PaymentRequest paymentRequest) throws IOException {

        WebSocketSession session = webSocketHandler.getSession(paymentRequest.getTransactionId());
        validationSession(session);

        String token = paymentRequest.getToken();
        paymentTokenUtils.validateToken(token);
        Long userId = paymentTokenUtils.extractUserId(token);

        Payment payment = paymentRepository
                .findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        Franchise franchise = franchiseService.getFranchise(paymentRequest.getFranchiseCode());

        int creditLimit = payment.getCreditLimit();
        int currentMonthDebt = payment.getCurrentMonthDebt();
        int leftCreditLimit = creditLimit - (currentMonthDebt + paymentRequest.getPaymentAmount());

        if (creditLimit < currentMonthDebt + paymentRequest.getPaymentAmount()) {
            throw new ExceededCreditLimitException(franchise.getName(), paymentRequest.getPaymentAmount(), "한도 초과");
        }

        payment.plusCurrentMonthDebt(paymentRequest.getPaymentAmount());

        PaymentHistoryDTO paymentHistoryDTO = PaymentHistoryDTO.builder()
                .userId(payment.getUserId())
                .paymentAmount(paymentRequest.getPaymentAmount())
                .franchise(franchise)
                .build();
        PaymentHistory paymentHistory = paymentHistoryDTO.toEntity();

        paymentTokenUtils.deleteToken(token);
        paymentRepository.save(payment);
        paymentHistoryService.savePaymentHistory(paymentHistory);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .franchiseName(franchise.getName())
                .paymentAmount(paymentRequest.getPaymentAmount())
                .date(LocalDateTime.now())
                .leftCreditLimit(leftCreditLimit)
                .message("결제가 완료되었습니다.")
                .build();

        String message = convertMessageToJson(paymentResponse);

        session.sendMessage(new TextMessage(message));
        session.close();

        return paymentResponse;
    }

    public PaymentTokenResponse createPaymentToken(PaymentAuthRequest paymentAuthRequest) {

        Long reqUserId = paymentAuthRequest.getUserId();
        Payment payment = paymentRepository
                .findPaymentByUserId(reqUserId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        String reqPaymentPassword = paymentAuthRequest.getPaymentPassword();
        if (!payment.getPassword().equals(reqPaymentPassword)) {
            throw new PaymentPasswordMismatchException("간편 비밀번호가 틀렸습니다.");
        }

        String createdToken = paymentTokenUtils.createToken(reqUserId);

        return PaymentTokenResponse.builder()
                .token(createdToken)
                .build();
    }

    private String convertMessageToJson(PaymentResponse paymentResponse) throws ConvertMessageToJsonException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            return objectMapper.writeValueAsString(paymentResponse);
        } catch (JsonProcessingException e) {
            throw new ConvertMessageToJsonException("응답 메시지 직렬화 실패", e);
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
