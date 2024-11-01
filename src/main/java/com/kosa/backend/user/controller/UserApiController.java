package com.kosa.backend.user.controller;

import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
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

    // 회원가입
    @PostMapping("/api/signup")
    public ResponseEntity signup(@RequestBody UserDTO userDTO) {
        Long num = userService.save(userDTO);
        return ResponseEntity.ok()
                .body(num);
    }

    // 로그인
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody UserDTO uerDTO) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(uerDTO.getEmail(), uerDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 인증이 성공했다면 사용자 정보를 가져옵니다
            User user = (User) authentication.getPrincipal();
            // 필요한 추가 작업 (예: JWT 토큰 발급) 수행

            return ResponseEntity.ok("로그인 성공");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }

    // 로그아웃
    @GetMapping("/api/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        /*
            SecurityContextLogoutHandler:Spring Security에서 제공하는 로그아웃 핸들러
            핸들러는 사용자의 세션을 종료하고 인증 정보를 삭제함.
            logout() 메서드는 request, response,
            그리고 현재 사용자 인증 정보를 담고 있는 Authentication 객체를 인자로 받음
            이 인증 정보로 로그아웃, 세션 무효화, 쿠키게 저장된 인증 정보 제거함.
         */
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok("message : logout Successful");
    }

    @GetMapping("/api/test")
    public ResponseEntity test() {
        return ResponseEntity.ok().body("Hello World");
    }
}
