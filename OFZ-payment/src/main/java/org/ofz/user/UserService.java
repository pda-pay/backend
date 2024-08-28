package org.ofz.user;

import org.ofz.user.dto.UserLoginReq;
import org.ofz.user.dto.UserSignupReq;
import org.ofz.user.dto.UserValidateLoginIdReq;
import org.ofz.user.exception.SignupDuplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAvailableLoginId(UserValidateLoginIdReq userValidateLoginIdReq) {
        boolean isExist = userRepository.existsByLoginId(userValidateLoginIdReq.getLoginId());
        return !isExist;
    }

//    public String requestSms(UserSmsReq userSmsReq) {
//
//    }
//
//    public boolean validateSms(UserSmsReq userSmsReq) {
//
//    }

    public void signup(UserSignupReq userSignupReq) {
        boolean isExistingLoginId = userRepository.existsByLoginId(userSignupReq.getLoginId());
        boolean isExistingPhoneNumber = userRepository.existsByPhoneNumber(userSignupReq.getPhoneNumber());
        if(isExistingLoginId && isExistingPhoneNumber) throw new SignupDuplicationException("회원가입 실패. 아이디 및 전화번호 중복");
        if(isExistingLoginId) throw new SignupDuplicationException("회원가입 실패. 아이디 중복");
        if(isExistingPhoneNumber) throw new SignupDuplicationException("회원가입 실패. 전화번호 중복");
        userRepository.save(userSignupReq.toEntity());
    }

    public boolean login(UserLoginReq userLoginReq) {
        return userRepository.findByLoginId(userLoginReq.getLoginId())
                .stream()
                .allMatch(user -> user.getPassword().equals(userLoginReq.getPassword()));
    }
}
