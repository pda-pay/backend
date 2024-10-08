package org.ofz.smsAuth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneNumberForVerificationReq {
    @NotBlank(message = "전화번호 부재")
    @Pattern(regexp = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$", message = "전화번호 양식 틀림")
    private String phoneNumber;
}
