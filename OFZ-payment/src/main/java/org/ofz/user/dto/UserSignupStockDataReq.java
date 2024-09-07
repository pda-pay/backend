package org.ofz.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupStockDataReq {
    @NotBlank(message = "이름 미입력")
    private String name;

    @NotBlank(message = "전화번호 미입력")
    @Pattern(regexp = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$", message = "전화번호 양식 틀림")
    private String phoneNumber;
}
