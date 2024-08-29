package org.ofz.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserValidateLoginIdReq {
    @NotBlank
    private String loginId;
}
