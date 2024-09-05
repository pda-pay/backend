package org.ofz.repayment.exception;

import lombok.Builder;
import lombok.Getter;
import org.ofz.management.exception.FetchPreviousStockPriceException;
import org.ofz.repayment.exception.repayment.*;
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
    public ResponseEntity<ErrorResponseDTO> catchTooHighPrepaymentAmountException(TooHighPrepaymentAmountException e) {

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
    public ResponseEntity<ErrorResponseDTO> catchInvalidCashPrepaymentAmountException(InvalidPrepaymentAmountException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchStockPriorityNotFoundException(StockPriorityNotFoundException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchTooHighPawnStockQuantityException(TooHighPawnStockQuantityException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchMortgagedNotFoundException(MortgagedNotFoundException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchStockNotFoundException(StockNotFoundException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchInvalidPawnExistException(InvalidPawnExistException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchFetchRequestSellStockException(FetchRequestSellStockException e) {

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchFetchPreviousStockPriceException(FetchPreviousStockPriceException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
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
