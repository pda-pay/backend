package org.ofz.smsAuth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationCodeReq {
    private String code;
    private String phoneNumber;
}
