package com.kosa.backend.user.controller;

import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.dto.UserInfoDTO;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/user-info")
    public ResponseEntity<UserInfoDTO> getUserInfo(@AuthenticationPrincipal CustomUserDetails cud) {
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userService.getUserInfo(user.getId());
    }
}
