package com.kosa.backend.config.oauth2;

import com.kosa.backend.config.jwt.JWTUtil;
import com.kosa.backend.user.dto.CustomOAuth2User;
import com.kosa.backend.user.dto.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 통합된 CustomUserDetails 객체 사용
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // 사용자 이메일 가져오기
        String email = customUserDetails.getEmail();

        // 사용자 권한 가져오기
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String authority = authorities.iterator().next().getAuthority();

        // JWT 생성
        String token = jwtUtil.createJwt(email, authority, 60 * 60 * 60 * 10L);

        // 쿠키 생성 및 응답에 추가
        response.addCookie(createCookie("Authorization", token));
        response.sendRedirect("http://localhost:3000/cookie-to-header");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60 * 10); // 쿠키 유효 시간 설정
        cookie.setPath("/"); // 모든 경로에 쿠키 적용
        cookie.setHttpOnly(true); // JavaScript에서 접근 불가
        // cookie.setSecure(true); // HTTPS 환경에서만 사용 (필요 시 활성화)
        return cookie;
    }
}

