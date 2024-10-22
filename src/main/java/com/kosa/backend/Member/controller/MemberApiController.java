package com.kosa.backend.Member.controller;

import com.kosa.backend.Member.dto.MemberDTO;
import com.kosa.backend.Member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class MemberApiController {

    private final MemberService userService;

    // 회원가입
    @PostMapping("/api/signup")
    public ResponseEntity signup(@RequestBody MemberDTO memberDTO) {
        Long num = userService.save(memberDTO);
        return ResponseEntity.ok()
                .body(num);
    }

    @GetMapping("/api/test")
    public ResponseEntity test() {
        return ResponseEntity.ok().body("Hello World");
    }



}
