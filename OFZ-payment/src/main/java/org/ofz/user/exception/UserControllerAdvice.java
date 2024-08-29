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
    public ResponseEntity<UserErrorRes> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        List<String> errorMessages = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if(error instanceof FieldError){
                        return ((FieldError) error).getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .toList();
        String bindedMessage = String.join(", ", errorMessages);
        UserErrorRes responseBody = new UserErrorRes(LocalDateTime.now(), bindedMessage);
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    // 회원가입 아이디, 전화번호 중복 예외처리
    @ExceptionHandler(SignupDuplicationException.class)
    public ResponseEntity<UserErrorRes> handleSignupArgumentException(SignupDuplicationException ex){
        UserErrorRes responseBody = new UserErrorRes(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
