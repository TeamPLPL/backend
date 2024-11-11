package com.kosa.backend.user.controller;

import com.kosa.backend.config.jwt.JWTUtil;
import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class UserApiController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    // 회원가입
    @PostMapping("/api/signup")
    public ResponseEntity signup(@RequestBody UserDTO userDTO) {
        int num = userService.save(userDTO);
        return ResponseEntity.ok()
                .body(num);
    }

    // 소셜 로그인 시 쿠키에 있는 토큰 헤더로 리디렉트 컨트롤러
    @GetMapping("/api/cookie-to-header")
    public ResponseEntity<String> getJwtFromCookie(HttpServletRequest request) {
        // 쿠키에서 JWT 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    String token = cookie.getValue(); // 쿠키 값이 JWT 토큰

                    System.out.println(token);

                    // 헤더에 JWT를 포함하여 반환
                    return ResponseEntity.ok()
                            .header("Authorization", "Bearer " + token)
                            .body("JWT token sent in header");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token not found in cookies");
    }


    @GetMapping("/api/test")
    public ResponseEntity test() {
        return ResponseEntity.ok().body("Hello World");
    }
}
