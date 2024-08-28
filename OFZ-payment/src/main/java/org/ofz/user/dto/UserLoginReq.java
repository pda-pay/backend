package org.ofz.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginReq {
    @NotBlank
    private String loginId;

    @NotBlank
    private String password;
}
