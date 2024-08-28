package org.ofz.payment.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.ofz.payment.exception.payment.PaymentTokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

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

        Map<String, Long> user = new HashMap<>();
        user.put("userId", userId);

        return Jwts.builder()
                .setClaims(user)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 유저 id 추출
    public Long extractUserId(String token) {
        Claims claims = getClaims(token);

        return claims.get("userId", Long.class);
    }

    // 클레임 파싱
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 검증
    public void validateToken(String token) {

        try {
            Claims claims = getClaims(token);
        } catch (ExpiredJwtException e) {
            throw new PaymentTokenExpiredException("토큰이 만료되었습니다.");
        }
    }
}
