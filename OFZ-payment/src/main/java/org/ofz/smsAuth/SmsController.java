package org.ofz.smsAuth;

import com.netflix.discovery.converters.Auto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ofz.smsAuth.dto.PhoneNumberForVerificationRequest;
import org.ofz.smsAuth.dto.VerificationCodeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    // SMS 인증번호 요청
    @PostMapping("/users/sms-request")
    public ResponseEntity<HashMap<String, String>> getPhoneNumberForVerification(
            @RequestBody @Valid PhoneNumberForVerificationRequest request) {
        LocalDateTime sentAt = LocalDateTime.now();
        smsService.sendVerificationMessage(request.getPhoneNumber(), sentAt);

        // 응답 메시지를 HashMap으로 구성
        HashMap<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "SMS 인증번호가 요청되었습니다.");

        // 상태 코드 202 (Accepted)와 메시지를 HashMap으로 반환
        return new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
    }

    // SMS 인증번호 검증
    @PostMapping("/users/sms-verification")
    public ResponseEntity<HashMap<String, String>> verificationByCode(
            @RequestBody @Valid VerificationCodeRequest request) {
        LocalDateTime verifiedAt = LocalDateTime.now();
        smsService.verifyCode(request.getCode(), verifiedAt);

        // 응답 메시지를 HashMap으로 구성
        HashMap<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "정상 인증 되었습니다.");

        // 상태 코드 200 (OK)와 메시지를 HashMap으로 반환
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
