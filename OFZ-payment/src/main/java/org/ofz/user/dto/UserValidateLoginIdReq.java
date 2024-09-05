package org.ofz.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserValidateLoginIdReq {
    @NotBlank(message = "아이디 미입력")
    private String loginId;
}
