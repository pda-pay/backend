package org.ofz.repayment.exception;

import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RepaymentExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RepaymentExceptionHandler.class);

    @ExceptionHandler(RepaymentProcessingException.class)
    public ResponseEntity<ErrorResponseDTO> handleRepaymentProcessingException(RepaymentProcessingException ex) {
        logger.error("Repayment processing error: {}", ex.getMessage(), ex);
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleExternalServiceException(ExternalServiceException ex) {
        logger.error("External service error: {}", ex.getMessage(), ex);
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("External service error: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Getter
    @Builder
    private static class ErrorResponseDTO {
        private LocalDateTime timestamp;
        private String message;
    }
}



