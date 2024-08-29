package org.ofz.smsAuth.exception;

import org.ofz.smsAuth.dto.SmsAuthErrorRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackages = "org.ofz.smsAuth")
public class SmsExceptionHandler {

    // @Valid 검증 실패 시 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SmsAuthErrorRes> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 검증 실패한 메시지를 추출하여 리스트로 변환
        List<String> errorMessages = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .toList();

        // 리스트의 메시지를 ", "로 조합하여 하나의 문자열로 생성
        String bindedMessage = String.join(", ", errorMessages);

        // 커스텀 에러 응답 생성
        SmsAuthErrorRes errorResponse = new SmsAuthErrorRes(
                LocalDateTime.now(),
                bindedMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // VerificationCodeExpiredException 처리
    @ExceptionHandler(VerificationCodeExpiredException.class)
    public ResponseEntity<SmsAuthErrorRes> handleVerificationCodeExpiredException(VerificationCodeExpiredException ex) {
        SmsAuthErrorRes errorResponse = new SmsAuthErrorRes(
                LocalDateTime.now(),
                ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // VerificationCodeNotFoundException 처리
    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ResponseEntity<SmsAuthErrorRes> handleVerificationCodeNotFoundException(VerificationCodeNotFoundException ex) {
        SmsAuthErrorRes errorResponse = new SmsAuthErrorRes(
                LocalDateTime.now(),
                ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // SmsSendingFailedException 처리
    @ExceptionHandler(SmsSendingFailedException.class)
    public ResponseEntity<SmsAuthErrorRes> handleSmsSendingFailedException(SmsSendingFailedException ex) {
        SmsAuthErrorRes errorResponse = new SmsAuthErrorRes(
                LocalDateTime.now(),
                ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 모든 예외에 대한 기본 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SmsAuthErrorRes> handleAllExceptions(Exception ex) {
        SmsAuthErrorRes errorResponse = new SmsAuthErrorRes(
                LocalDateTime.now(),
                "An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
