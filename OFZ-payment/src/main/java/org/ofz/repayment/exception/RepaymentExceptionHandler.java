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

@RestControllerAdvice
public class RepaymentExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchUserNotFoundException(UserNotFoundException e) {

        ErrorDTO error = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchTooHighCashPrepaymentAmountException(TooHighCashPrepaymentAmountException e) {

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchWebClientIllegalArgumentException(WebClientIllegalArgumentException e) {

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage() + " " + e.getErrorMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchWebClientResponseNullPointerException(WebClientResponseNullPointerException e) {

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchWebClientErrorException(WebClientErrorException e) {

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, e.getCode());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchWebClientServerErrorException(WebClientServerErrorException e) {

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, e.getCode());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchWebClientBadRequestException(WebClientBadRequestException e) {

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchNoRepaymentRecordsException(NoRepaymentRecordsException e) {

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> catchInvalidCashPrepaymentAmountException(InvalidCashPrepaymentAmountException e) {

        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
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
}
