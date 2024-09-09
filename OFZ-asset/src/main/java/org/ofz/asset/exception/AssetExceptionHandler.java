package org.ofz.asset.exception;

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
public class AssetExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AssetExceptionHandler.class);

    // NoDataFoundException에 대한 핸들러
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoDataFoundException(NoDataFoundException ex) {
        logger.error("데이터가 존재하지 않습니다: {}", ex.getMessage());
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .message("데이터가 존재하지 않습니다: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    // ErrorResponseDTO 정의
    @Getter
    @Builder
    private static class ErrorResponseDTO {
        private LocalDateTime timestamp;
        private String message;
    }
}
