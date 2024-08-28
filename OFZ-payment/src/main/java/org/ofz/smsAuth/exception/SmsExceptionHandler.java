package org.ofz.smsAuth.exception;

import org.ofz.smsAuth.dto.SmsAuthErrorRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "org.ofz.smsAuth")
public class SmsExceptionHandler {

    // VerificationCodeExpiredException 처리
    @ExceptionHandler(VerificationCodeExpiredException.class)
    public ResponseEntity<SmsAuthErrorRes> handleVerificationCodeExpiredException(VerificationCodeExpiredException ex) {
        SmsAuthErrorRes errorResponse = new SmsAuthErrorRes(
                LocalDateTime.now(),
                ex.getMessage(),
                "CODE_EXPIRED"  // 예외에 맞는 에러 코드 추가
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // VerificationCodeNotFoundException 처리
    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ResponseEntity<SmsAuthErrorRes> handleVerificationCodeNotFoundException(VerificationCodeNotFoundException ex) {
        SmsAuthErrorRes errorResponse = new SmsAuthErrorRes(
                LocalDateTime.now(),
                ex.getMessage(),
                "CODE_NOT_FOUND"  // 예외에 맞는 에러 코드 추가
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // SmsSendingFailedException 처리
    @ExceptionHandler(SmsSendingFailedException.class)
    public ResponseEntity<SmsAuthErrorRes> handleSmsSendingFailedException(SmsSendingFailedException ex) {
        SmsAuthErrorRes errorResponse = new SmsAuthErrorRes(
                LocalDateTime.now(),
                ex.getMessage(),
                "SMS_SEND_FAILED"  // 예외에 맞는 에러 코드 추가
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 모든 예외에 대한 기본 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SmsAuthErrorRes> handleAllExceptions(Exception ex) {
        SmsAuthErrorRes errorResponse = new SmsAuthErrorRes(
                LocalDateTime.now(),
                "An unexpected error occurred: " + ex.getMessage(),
                "UNKNOWN_ERROR"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
