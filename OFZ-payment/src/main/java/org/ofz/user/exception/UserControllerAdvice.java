package org.ofz.user.exception;

import org.ofz.user.dto.UserErrorRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice(basePackages = "org.ofz.user")
public class UserControllerAdvice {

    // @valid 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserErrorRes> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
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
        String boundMessage = String.join(", ", errorMessages);
        UserErrorRes responseBody = new UserErrorRes(LocalDateTime.now(), boundMessage);
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    // 회원가입 아이디, 전화번호 중복 예외처리
    @ExceptionHandler(SignupDuplicationException.class)
    public ResponseEntity<UserErrorRes> handleSignupArgumentException(SignupDuplicationException ex) {
        UserErrorRes responseBody = new UserErrorRes(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    // 로그인 시도 유저가 유효하지 않은 경우 예외처리
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<UserErrorRes> handleInvalidCredentialsException(InvalidCredentialsException ex){
        UserErrorRes responseBody = new UserErrorRes(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
    }

    // 회원가입 시 partner API 호출 예외 처리
    @ExceptionHandler(SignupPartnerApiCallException.class)
    public ResponseEntity<UserErrorRes> handleApiCallException(SignupPartnerApiCallException ex) {
        UserErrorRes responseBody = new UserErrorRes(LocalDateTime.now(), "API 호출 중 오류가 발생했습니다: " + ex.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // 주식 데이터 저장 예외 처리
    @ExceptionHandler(SignupStockDataSaveException.class)
    public ResponseEntity<UserErrorRes> handleDataSaveException(SignupStockDataSaveException ex) {
        UserErrorRes responseBody = new UserErrorRes(LocalDateTime.now(), "데이터 저장 중 오류가 발생했습니다: " + ex.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
