package org.ofz.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ofz.jwt.JwtToken;
import org.ofz.jwt.JwtTokenProvider;
import org.ofz.user.dto.UserLoginReq;
import org.ofz.user.dto.UserLoginRes;
import org.ofz.user.dto.UserSignupReq;
import org.ofz.user.dto.UserValidateLoginIdReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

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
    public ResponseEntity<UserLoginRes> login(@RequestBody @Valid UserLoginReq userLoginReq, HttpServletResponse response){
        UserLoginRes userLoginRes = userService.login(userLoginReq);

        JwtToken jwtToken = jwtTokenProvider.generateToken(userLoginReq.getLoginId());

        ResponseCookie accessTokenCookie = ResponseCookie.from( "accessToken", jwtToken.getAccessToken())
                .path("/")
//                .domain("localhost")
                .maxAge(30 * 60 * 60)
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();


        response.addHeader("Set-Cookie", accessTokenCookie.toString());

        return new ResponseEntity<>(userLoginRes, HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping("/users/logout")
    public ResponseEntity<Void> logout(@CookieValue("accessToken") String accessToken){
        userService.logout(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
