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

    public String createToken(Long userId) {

        long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
        String id = String.valueOf(userId);

        String token = Jwts.builder()
                .setSubject(id)
                .setExpiration(new Date(expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        redisUtil.addPaymentToken(PAYMENT_PREFIX + token, EXPIRATION_TIME);

        return token;
    }

    public Long extractUserId(String token) {

        Claims claims = getClaims(token);
        String userId = claims.getSubject();

        return Long.parseLong(userId);
    }

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

    public void validateToken(String token) {

        if (!redisUtil.validatePaymentToken(PAYMENT_PREFIX + token)) {
            throw new PaymentTokenExpiredException("토큰이 만료되었습니다.");
        }

        try {
            Claims claims = getClaims(token);

        } catch (ExpiredJwtException e) {
            throw new PaymentTokenExpiredException("토큰이 만료되었습니다.");
        } catch (JwtException e) {
            throw new NonValidPaymentTokenException("유효한 토큰이 아닙니다.");
        }
    }
}
