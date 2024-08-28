package org.ofz.payment.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ofz.payment.exception.websocket.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchSocketIdNullException(SocketIdNullException e) {
        log.error("exception class: {}", e.getClass());

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchWebSocketSessionNotFoundException(WebSocketSessionNotFoundException e) {
        log.error("exception class: {}", e.getClass());

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchInvalidWebSocketSessionException(InvalidWebSocketSessionException e) {
        log.error("exception class: {}", e.getClass());

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.GONE);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchInvalidUriException(InvalidUriException e) {
        log.error("exception class: {}", e.getClass());

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchConvertMessageToJsonException(ConvertMessageToJsonException e) {
        log.error("exception class: {}", e.getClass());

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage() + " " + e.getCause().getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchPaymentIOException(PaymentIOException e) {
        log.error("exception class: {}", e.getClass());

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage() + " " + e.getCause().getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchPaymentNotFoundException(PaymentNotFoundException e) {
        log.error("exception class: {}", e.getClass());

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<PaymentFailDTO> catchExceededCreditLimitException(ExceededCreditLimitException e) {
        log.error("exception class: {}", e.getClass());

        PaymentFailDTO failDTO = PaymentFailDTO.builder()
                .franchiseName(e.getFranchiseName())
                .triedAmount(e.getTriedAmount())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(failDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchFranchiseNotFoundException(FranchiseNotFoundException e) {
        log.error("exception class: {}", e.getClass());

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @Getter
    @Builder
    private static class ErrorDTO {

        private String message;

        public ErrorDTO() {}

        public ErrorDTO(String message) {
            this.message = message;
        }
    }

    @Getter
    @Builder
    private static class PaymentFailDTO {
        private String franchiseName;
        private int triedAmount;
        private String message;

        public PaymentFailDTO() {}

        public PaymentFailDTO(String franchiseName, int triedAmount, String message) {
            this.franchiseName = franchiseName;
            this.triedAmount = triedAmount;
            this.message = message;
        }
    }
}
