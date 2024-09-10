package org.ofz.payment.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ofz.payment.exception.franchise.FranchiseNotFoundException;
import org.ofz.payment.exception.franchise.FranchisePasswordMismatchException;
import org.ofz.payment.exception.history.MissingParameterException;
import org.ofz.payment.exception.payment.*;
import org.ofz.payment.exception.websocket.*;
import org.ofz.rabbitMQ.NotificationPage;
import org.ofz.rabbitMQ.NotificationType;
import org.ofz.rabbitMQ.Publisher;
import org.ofz.rabbitMQ.rabbitDto.NotificationMessage;
import org.ofz.rabbitMQ.rabbitDto.SimplePaymentLogDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class PaymentExceptionHandler {

    private final Publisher<NotificationMessage> notifyPublisher;
    private final Publisher<SimplePaymentLogDTO> publisher;

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchSocketIdNullException(SocketIdNullException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchWebSocketSessionNotFoundException(WebSocketSessionNotFoundException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchInvalidWebSocketSessionException(InvalidWebSocketSessionException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.GONE);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchInvalidUriException(InvalidUriException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchConvertMessageToJsonException(ConvertMessageToJsonException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage() + " " + e.getCause().getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchPaymentIOException(PaymentIOException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage() + " " + e.getCause().getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchPaymentNotFoundException(PaymentNotFoundException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<PaymentFailResponseDTO> catchExceededCreditLimitException(ExceededCreditLimitException e) {
        log.error("exception class: {}", e.getClass());

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .loginId(e.getUser().getLoginId())
                .title("간편 결제")
                .body("한도 초과로 결제에 실패하였습니다.\n[결제 시도 금액: " + e.getTriedAmount() + "]\n[남은 한도: " + e.getLeftCreditLimit() + "]")
                .category(NotificationType.결제)
                .page(NotificationPage.MAIN)
                .build();

        notifyPublisher.sendMessage(notificationMessage);

        publisher.sendMessage(SimplePaymentLogDTO.builder()
                .id(0L)
                .loginId(e.getUser().getLoginId())
                .amount(e.getTriedAmount())
                .franchiseCode(e.getFranchise().getCode())
                .isSuccess("잔액 부족")
                .date(LocalDateTime.now())
                .build());

        PaymentFailResponseDTO failDTO = PaymentFailResponseDTO.builder()
                .franchiseName(e.getFranchise().getName())
                .triedAmount(e.getTriedAmount())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(failDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchFranchiseNotFoundException(FranchiseNotFoundException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchPaymentPasswordMismatchException(PaymentPasswordMismatchException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchPaymentTokenExpiredException(PaymentTokenExpiredException e) {
        log.error("exception class: {}", e.getClass());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchNonValidPaymentTokenException(NonValidPaymentTokenException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchFranchisePasswordMismatchException(FranchisePasswordMismatchException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchMissingParameterException(MissingParameterException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchPaymentRestrictedUserException(PaymentRestrictedUserException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.FORBIDDEN);
    }

    @Getter
    private static class ErrorResponseDTO {

        private LocalDateTime timestamp;
        private String message;

        public ErrorResponseDTO() {
        }

        @Builder
        public ErrorResponseDTO(String message) {
            this.timestamp = LocalDateTime.now();
            this.message = message;
        }
    }

    @Getter
    private static class PaymentFailResponseDTO {

        private LocalDateTime timestamp;
        private String franchiseName;
        private int triedAmount;
        private String message;

        public PaymentFailResponseDTO() {
        }

        @Builder
        public PaymentFailResponseDTO(String franchiseName, int triedAmount, String message) {
            this.timestamp = LocalDateTime.now();
            this.franchiseName = franchiseName;
            this.triedAmount = triedAmount;
            this.message = message;
        }
    }
}
