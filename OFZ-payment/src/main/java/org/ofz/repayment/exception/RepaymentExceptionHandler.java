package org.ofz.repayment.exception;

import lombok.Builder;
import lombok.Getter;
import org.ofz.repayment.exception.repayment.InvalidCashPrepaymentAmountException;
import org.ofz.repayment.exception.repayment.NoRepaymentRecordsException;
import org.ofz.repayment.exception.repayment.TooHighCashPrepaymentAmountException;
import org.ofz.repayment.exception.user.UserNotFoundException;
import org.ofz.repayment.exception.webclient.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RepaymentExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchUserNotFoundException(UserNotFoundException e) {

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchTooHighCashPrepaymentAmountException(TooHighCashPrepaymentAmountException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchWebClientIllegalArgumentException(WebClientIllegalArgumentException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage() + " " + e.getErrorMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchWebClientResponseNullPointerException(WebClientResponseNullPointerException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchWebClientErrorException(WebClientErrorException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, e.getCode());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchWebClientServerErrorException(WebClientServerErrorException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, e.getCode());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchWebClientBadRequestException(WebClientBadRequestException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchNoRepaymentRecordsException(NoRepaymentRecordsException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchInvalidCashPrepaymentAmountException(InvalidCashPrepaymentAmountException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @Getter
    private static class ErrorResponseDTO {

        private LocalDateTime timestamp;
        private String message;

        public ErrorResponseDTO() {}

        @Builder
        public ErrorResponseDTO(String message) {
            this.timestamp = LocalDateTime.now();
            this.message = message;
        }
    }
}
