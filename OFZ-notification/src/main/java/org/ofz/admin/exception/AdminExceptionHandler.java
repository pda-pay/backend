package org.ofz.admin.exception;

import lombok.Builder;
import lombok.Getter;
import org.ofz.admin.exception.mq.RepaymentMQException;
import org.ofz.admin.exception.mq.SimplePaymentMQException;
import org.ofz.admin.exception.sse.RepaymentSseException;
import org.ofz.admin.exception.sse.SimplePaymentSseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AdminExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchRepaymentMQException(RepaymentMQException e) {

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchSimplePaymentMQException(SimplePaymentMQException e) {

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchRepaymentSseException(RepaymentSseException e) {

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchSimplePaymentSseException(SimplePaymentSseException e) {

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
}
