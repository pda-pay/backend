package org.ofz.smsAuth.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {
    private String phoneNumber;
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }

    public String generateCodeMessage() {
        return String.format("[140PAY] 인증번호 [%s] 본인확인을 위해 입력해주세요.", code);
    }
}
