package org.ofz.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.ofz.user.User;

@Getter
public class UserSignupReq {
    @NotBlank(message = "아이디 미입력")
    private String loginId;

    @NotBlank(message = "비밀번호 미입력")
    @Size(min = 8, message = "비밀번호 8자리 미만")
    private String password;

    @NotBlank(message = "이름 미입력")
    private String name;

    @NotBlank(message = "전화번호 미입력")
    @Pattern(regexp = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$", message = "전화번호 양식 틀림")
    private String phoneNumber;

    public User toEntity(String encodedPassword){
        return User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();
    }
}
