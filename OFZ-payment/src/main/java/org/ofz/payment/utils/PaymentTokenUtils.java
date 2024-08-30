package org.ofz.payment.utils;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.ofz.payment.exception.payment.NonValidPaymentTokenException;
import org.ofz.payment.exception.payment.PaymentTokenExpiredException;
import org.ofz.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class PaymentTokenUtils {

    private static final String PAYMENT_PREFIX = "payment:";

    private final RedisUtil redisUtil;

    @Value("${secret.key.base64}")
    private String SECRET_KEY_BASE64;
    private SecretKey secretKey;
    private static final long EXPIRATION_TIME = 60 * 1000;

    @PostConstruct
    public void init() {
        this.secretKey = new SecretKeySpec(
                Base64.getDecoder().decode(SECRET_KEY_BASE64),
                SignatureAlgorithm.HS256.getJcaName()
        );
    }

    // 토큰 생성
    public String createToken(Long userId) {

        long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
        String id = String.valueOf(userId);

        String token = PAYMENT_PREFIX + Jwts.builder()
                .setSubject(id)
                .setExpiration(new Date(expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        redisUtil.addPaymentToken(token, EXPIRATION_TIME);

        return token;
    }

    // 토큰에서 유저 id 추출
    public Long extractUserId(String paymentToken) {

        String token = separateToken(paymentToken);

        Claims claims = getClaims(token);
        String userId = claims.getSubject();

        return Long.parseLong(userId);
    }

    // 클레임 파싱
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void deleteToken(String token) {
        redisUtil.deletePaymentToken(token);
    }

    // 토큰 검증
    public void validateToken(String paymentToken) {

        if (!redisUtil.validatePaymentToken(paymentToken)) {
            throw new PaymentTokenExpiredException("토큰이 만료되었습니다.");
        }

        String token = separateToken(paymentToken);

        try {
            Claims claims = getClaims(token);

        } catch (ExpiredJwtException e) {
            throw new PaymentTokenExpiredException("토큰이 만료되었습니다.");
        } catch (JwtException e) {
            throw new NonValidPaymentTokenException("유효한 토큰이 아닙니다.");
        }
    }

    private String separateToken(String paymentToken) {

        return paymentToken.split(":")[1];
    }
}
