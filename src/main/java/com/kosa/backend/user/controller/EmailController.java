package com.kosa.backend.user.controller;

import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.EmailService;
import com.kosa.backend.user.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class EmailController {
    private final UserService userService;
    private final EmailService emailService;

    // 이메일 입력해서 인증
    @PostMapping("/auth/email")
    public ResponseEntity<?> email(@RequestBody UserDTO userDTO) throws MessagingException, UnsupportedEncodingException {
        Map<String, String> response = new HashMap<>();
        User user = userService.authenticate(userDTO.getEmail());
        System.out.println(userDTO.getEmail());
        if (user != null) {
            System.out.println("회원이 존재합니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict 상태 반환
        } else {
            String key = emailService.sendHtmlEmail(userDTO.getEmail());
            response.put("key", key);
            return ResponseEntity.ok(response); // 200 OK 상태 반환
        }
    }

    // 등록된 이메일로 인증
    @GetMapping("/auth/useremail")
    public ResponseEntity<?> verifByuserEmail(@AuthenticationPrincipal CustomUserDetails customUserDetails) throws MessagingException, UnsupportedEncodingException {
        Map<String, String> response = new HashMap<>();
        User user = userService.findByEmail(customUserDetails.getUsername());
        String key = emailService.sendHtmlEmail(user.getEmail());
        response.put("key", key);
        return ResponseEntity.ok(response); // 200 OK 상태 반환
    }
}
