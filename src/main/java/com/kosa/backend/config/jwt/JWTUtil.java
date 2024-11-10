package com.kosa.backend.config.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    // secretKey 초기화, application.yml에 secret를 가져와서 HS256 방식으로 암호화 한다.
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("Authority", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String email, String authority, Long expiredMs) {
        // clockSkewMs는 JWT의 유효 기간 계산 시 약간의 시간 오차를 허용
        // 클라이언트와 서버 간 시간 차이, 네트워크 지연 등으로 인해 정확한 시간 동기화가 어려운 상황을 고려
        long clockSkewMs = 60000; // 60초의 clock skew

        return Jwts.builder()
                .claim("email", email)
                .claim("Authority", authority)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs + clockSkewMs))
                .signWith(secretKey)
                .compact();
    }
}
