package org.ofz.smsAuth.auth;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class VerificationCodeRepositoryImpl implements VerificationCodeRepository {

    // 임시 저장소 (HashMap을 이용한 간단한 예시)
    private final Map<String, VerificationCode> store = new ConcurrentHashMap<>();

    @Override
    public void save(VerificationCode verificationCode) {
        // 중복 요청 처리: 기존 코드가 있으면 갱신, 없으면 새로 저장
        if (store.containsKey(verificationCode.getPhoneNumber())) {
            store.replace(verificationCode.getPhoneNumber(), verificationCode);
        } else {
            store.put(verificationCode.getPhoneNumber(), verificationCode);
        }
    }

    @Override
    public Optional<VerificationCode> findByCode(String phoneNumber,String code) {
        return Optional.ofNullable(store.get(phoneNumber))
                .filter(vc -> vc.getCode().equals(code));
    }

    @Override
    public Optional<VerificationCode> findByPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(store.get(phoneNumber)); // 전화번호로 인증 코드 찾기
    }

    @Override
    public void remove(VerificationCode verificationCode) {
        store.remove(verificationCode.getPhoneNumber());
    }
}
