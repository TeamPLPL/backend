package com.kosa.backend.user.controller;

import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/api/signup")
    public ResponseEntity signup(@RequestBody UserDTO userDTO) {
        System.out.println("여기 들어옴");
        Long num = userService.save(userDTO);
        return ResponseEntity.ok()
                .body(num);
    }

    @GetMapping("/api/test")
    public ResponseEntity test() {
        return ResponseEntity.ok().body("Hello World");
    }
}
