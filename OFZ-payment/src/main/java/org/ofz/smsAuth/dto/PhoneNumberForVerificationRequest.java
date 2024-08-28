package org.ofz.smsAuth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneNumberForVerificationRequest {
    private String phoneNumber;
}
