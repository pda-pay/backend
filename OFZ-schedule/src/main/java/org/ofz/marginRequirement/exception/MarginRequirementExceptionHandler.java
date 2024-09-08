package org.ofz.marginRequirement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@RestControllerAdvice
public class MarginRequirementExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MarginRequirementExceptionHandler.class);

    // StockInformationNotFoundException에 대한 핸들러
    @ExceptionHandler(StockInformationNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleStockInformationNotFoundException(StockInformationNotFoundException ex) {
        logger.error("주식 정보를 찾을 수 없습니다: {}", ex.getMessage());
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("주식 정보를 찾을 수 없습니다: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // PriceNotFoundException에 대한 핸들러
    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlePriceNotFoundException(PriceNotFoundException ex) {
        logger.error("가격 정보를 찾을 수 없습니다: {}", ex.getMessage());
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("가격 정보를 찾을 수 없습니다: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // CreditLimitException에 대한 핸들러
    @ExceptionHandler(CreditLimitException.class)
    public ResponseEntity<ErrorResponseDTO> handleCreditLimitException(CreditLimitException ex) {
        logger.error("신용 한도 오류: {}", ex.getMessage());
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("신용 한도 오류: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // NullPointerException에 대한 핸들러 추가
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponseDTO> handleNullPointerException(NullPointerException ex) {
        logger.error("널 포인터 예외: {}", ex.getMessage(), ex);
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("널 포인터 예외: 필요한 객체나 데이터가 없습니다.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // IllegalArgumentException에 대한 핸들러 추가
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("잘못된 인자: {}", ex.getMessage(), ex);
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("잘못된 인자: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // DataAccessException (데이터베이스 관련 예외)에 대한 핸들러 추가
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataAccessException(DataAccessException ex) {
        logger.error("데이터베이스 접근 오류: {}", ex.getMessage(), ex);
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("데이터베이스 오류: 데이터베이스 접근 중 오류가 발생했습니다.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 기타 예외에 대한 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        logger.error("예기치 못한 오류가 발생했습니다: {}", ex.getMessage(), ex);
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("서버 내부 오류: 예기치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ErrorResponseDTO 정의
    @Getter
    @Builder
    private static class ErrorResponseDTO {
        private LocalDateTime timestamp;
        private String message;
    }
}
