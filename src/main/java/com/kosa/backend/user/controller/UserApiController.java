package com.kosa.backend.user.controller;

import com.kosa.backend.config.jwt.JWTUtil;
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
    private final JWTUtil jwtUtil;

    // 회원가입
    @PostMapping("/api/signup")
    public ResponseEntity signup(@RequestBody UserDTO userDTO) {
        int num = userService.save(userDTO);
        return ResponseEntity.ok()
                .body(num);
    }

    @GetMapping("/api/test")
    public ResponseEntity test() {
        return ResponseEntity.ok().body("Hello World");
    }
}
