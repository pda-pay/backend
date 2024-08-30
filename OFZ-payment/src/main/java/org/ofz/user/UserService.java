package org.ofz.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ofz.jwt.JwtToken;
import org.ofz.jwt.JwtTokenProvider;
import org.ofz.redis.RedisUtil;
import org.ofz.user.dto.UserLoginReq;
import org.ofz.user.dto.UserSignupReq;
import org.ofz.user.dto.UserValidateLoginIdReq;
import org.ofz.user.exception.InvalidCredentialsException;
import org.ofz.user.exception.SignupDuplicationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    @Transactional
    public boolean isAvailableLoginId(UserValidateLoginIdReq userValidateLoginIdReq) {
        boolean isExist = userRepository.existsByLoginId(userValidateLoginIdReq.getLoginId());
        return !isExist;
    }

    @Transactional
    public void signup(UserSignupReq userSignupReq) {
        boolean isExistingLoginId = userRepository.existsByLoginId(userSignupReq.getLoginId());
        boolean isExistingPhoneNumber = userRepository.existsByPhoneNumber(userSignupReq.getPhoneNumber());

        if(isExistingLoginId && isExistingPhoneNumber) throw new SignupDuplicationException("회원가입 실패. 아이디 및 전화번호 중복");
        if(isExistingLoginId) throw new SignupDuplicationException("회원가입 실패. 아이디 중복");
        if(isExistingPhoneNumber) throw new SignupDuplicationException("회원가입 실패. 전화번호 중복");

        String encodedPassword = passwordEncoder.encode(userSignupReq.getPassword());
        User user = userSignupReq.toEntity(encodedPassword);
        userRepository.save(user);
    }

    @Transactional
    public JwtToken login(UserLoginReq userLoginReq){
        return userRepository.findByLoginId(userLoginReq.getLoginId())
                .filter(user -> passwordEncoder.matches(userLoginReq.getPassword(), user.getPassword()))
                .map(user -> jwtTokenProvider.generateToken(user.getLoginId()))
                .orElseThrow(() -> new InvalidCredentialsException("유효하지 않은 아이디입니다."));
    }

    @Transactional
    public void logout(String accessToken){
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisUtil.addBlackList(accessToken, expiration);
    }
}
