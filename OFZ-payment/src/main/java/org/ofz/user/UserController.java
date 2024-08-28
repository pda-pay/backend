package org.ofz.user;

import jakarta.validation.Valid;
import org.ofz.user.dto.UserLoginReq;
import org.ofz.user.dto.UserSignupReq;
import org.ofz.user.dto.UserValidateLoginIdReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ID 중복검사 // true면 사용 가능한 ID
    @ResponseBody
    @PostMapping("/users/validation")
    public ResponseEntity<HashMap<String, Boolean>> isAvailableLoginId(@RequestBody @Valid UserValidateLoginIdReq userValidateLoginIdReq){
        boolean isAvailable = userService.isAvailableLoginId(userValidateLoginIdReq);
        HashMap<String, Boolean> responseBody = new HashMap<>();
        responseBody.put("isAvailable", isAvailable);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

//    // 인증번호 요청
//    @ResponseBody
//    @PostMapping("/users/sms-request")
//    public ResponseEntity<String> requestSms(@RequestBody UserSmsReq userSmsReq){
//        String certificationNumber = userService.requestSms(userSmsReq);
//        return new ResponseEntity<>(certificationNumber, HttpStatus.OK);
//    }
//
//    // 인증번호 검증
//    @ResponseBody
//    @PostMapping("/users/sms-validation")
//    public ResponseEntity<Boolean> validateSms(@RequestBody UserSmsReq userSmsReq){
//        boolean isValid = userService.validateSms(userSmsReq);
//        return new ResponseEntity<>(isValid, HttpStatus.OK);
//    }

    // 회원가입
    @ResponseBody
    @PostMapping("/users/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid UserSignupReq userSignupReq){
        userService.signup(userSignupReq);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 로그인 // jwt & 쿠키 발급 추가해야 함
    @ResponseBody
    @PostMapping("/users/login")
    public ResponseEntity<Boolean> login(@RequestBody @Valid UserLoginReq userLoginReq){
        boolean isSuccess = userService.login(userLoginReq);
        return new ResponseEntity<>(isSuccess, HttpStatus.OK);
    }
}
