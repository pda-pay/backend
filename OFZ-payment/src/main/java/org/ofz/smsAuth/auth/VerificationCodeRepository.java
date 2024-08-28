package org.ofz.smsAuth.auth;

import java.util.Optional;

public interface VerificationCodeRepository {

    void save(VerificationCode verificationCode);

    Optional<VerificationCode> findByCode(String code);

    Optional<VerificationCode> findByPhoneNumber(String phoneNumber);

    void remove(VerificationCode verificationCode);
}
