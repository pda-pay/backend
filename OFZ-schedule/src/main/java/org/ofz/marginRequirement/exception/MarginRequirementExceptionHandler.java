package org.ofz.marginRequirement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MarginRequirementExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MarginRequirementExceptionHandler.class);

    // StockInformationNotFoundException에 대한 핸들러
    @ExceptionHandler(StockInformationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStockInformationNotFoundException(StockInformationNotFoundException ex) {
        logger.error("Stock information not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("STOCK_INFORMATION_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // PriceNotFoundException에 대한 핸들러
    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePriceNotFoundException(PriceNotFoundException ex) {
        logger.error("Price not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("PRICE_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // CreditLimitException에 대한 핸들러
    @ExceptionHandler(CreditLimitException.class)
    public ResponseEntity<ErrorResponse> handleCreditLimitException(CreditLimitException ex) {
        logger.error("Credit limit error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("CREDIT_LIMIT_ERROR", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 기타 예외에 대한 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please try again later.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ErrorResponse DTO 정의
    public static class ErrorResponse {
        private String errorCode;
        private String errorMessage;

        public ErrorResponse(String errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        // 필요시 추가 메서드
    }
}
