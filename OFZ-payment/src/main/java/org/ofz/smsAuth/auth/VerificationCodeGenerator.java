package org.ofz.smsAuth.auth;

import java.time.LocalDateTime;
import java.util.Random;

public class VerificationCodeGenerator {

    public static VerificationCode generateVerificationCode(String phoneNumber, LocalDateTime sentAt) {
        String code = generateRandomCode();
        LocalDateTime expiresAt = sentAt.plusMinutes(3);  // 코드 유효 기간 3분
        return new VerificationCode(phoneNumber, code, sentAt, expiresAt);
    }

    private static String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
