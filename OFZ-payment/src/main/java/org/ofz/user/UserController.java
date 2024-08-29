package org.ofz.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.ofz.jwt.JwtToken;
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

    // 회원가입
    @ResponseBody
    @PostMapping("/users/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid UserSignupReq userSignupReq){
        userService.signup(userSignupReq);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 로그인
    @ResponseBody
    @PostMapping("/users/login")
    public ResponseEntity<Void> login(@RequestBody @Valid UserLoginReq userLoginReq, HttpServletResponse response){
        JwtToken jwtToken = userService.login(userLoginReq);

        Cookie accessTokenCookie = new Cookie("accessToken", jwtToken.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(30 * 60 * 60);

        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtToken.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(30 * 60 * 60);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
