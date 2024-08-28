package org.ofz.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginReq {
    @NotBlank(message = "아이디 미입력")
    private String loginId;

    @NotBlank(message = "비밀번호 미입력")
    private String password;
}
