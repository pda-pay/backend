package org.ofz.smsAuth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.ofz.smsAuth.auth.VerificationCode;
import org.ofz.smsAuth.auth.VerificationCodeGenerator;
import org.ofz.smsAuth.auth.VerificationCodeRepository;
import org.ofz.smsAuth.auth.VerificationCodeRepositoryImpl;
import org.ofz.smsAuth.exception.SmsSendingFailedException;
import org.ofz.smsAuth.exception.VerificationCodeAlreadySentException;
import org.ofz.smsAuth.exception.VerificationCodeExpiredException;
import org.ofz.smsAuth.exception.VerificationCodeNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmsService {
    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.provider}")
    private String smsProvider;

    @Value("${coolsms.api.sender}")
    private String smsSender;

    private DefaultMessageService messageService; // = new DefaultMessageService(apiKey, apiSecret, smsProvider);

    private final VerificationCodeRepositoryImpl verificationCodeRepositoryImpl;

    @PostConstruct
    public void init(){
        messageService = NurigoApp.INSTANCE.initialize(
                apiKey,
                apiSecret,
                smsProvider
        );
    }

    public void sendVerificationMessage(String to, LocalDateTime sentAt){
        // 전화번호로 기존 인증 코드 조회
        Optional<VerificationCode> existingCode = verificationCodeRepositoryImpl.findByPhoneNumber(to);
        if (existingCode.isPresent() && !existingCode.get().isExpired(sentAt)) {
            throw new VerificationCodeAlreadySentException("이미 인증 코드가 발송되었습니다. 잠시 후 다시 시도해주세요.");
        }

        VerificationCode verificationCode = VerificationCodeGenerator.generateVerificationCode(to, sentAt);
        verificationCodeRepositoryImpl.save(verificationCode);

        String text = verificationCode.generateCodeMessage();
        Message message = SmsUtil.createMessage(smsSender, to, text);

        try {
            SmsUtil.sendMessage(messageService, message);
        } catch (Exception e) {
            // SMS 발송 실패 처리
            throw new SmsSendingFailedException("SMS 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    public void verifyCode(String phoneNumber,String code, LocalDateTime verifiedAt) {
        VerificationCode verificationCode = verificationCodeRepositoryImpl.findByCode(phoneNumber, code)
                .orElseThrow(() -> new VerificationCodeNotFoundException("유효하지 않은 인증 코드입니다."));

        if (verificationCode.isExpired(verifiedAt)) {
            throw new VerificationCodeExpiredException("인증 코드의 유효 기간이 지났습니다.");
        }

        verificationCodeRepositoryImpl.remove(verificationCode);
    }
}
