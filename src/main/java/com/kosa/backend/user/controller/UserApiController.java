package com.kosa.backend.user.controller;

import com.kosa.backend.config.jwt.JWTUtil;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.dto.requestDTO.RequestUserDTO;
import com.kosa.backend.user.dto.responsedto.ResponseUserDTO;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.MakerService;
import com.kosa.backend.user.service.UserService;
import com.kosa.backend.util.CommonUtils;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserApiController {
    private final UserService userService;
    private final MakerService makerService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody UserDTO userDTO) {
        int num = userService.save(userDTO);
        return ResponseEntity.ok()
                .body(num);
    }

    // 소셜 로그인 시 쿠키에 있는 토큰 헤더로 리디렉트 컨트롤러
    @GetMapping("/cookie-to-header")
    public ResponseEntity<String> getJwtFromCookie(HttpServletRequest request) {
        System.out.println("쿠키 호출");
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

    // 유저 정보 업데이트(이름, 닉네임, 메이커 소개)
    @PostMapping("/input/userinfo")
    public ResponseEntity<?> inputUserName(@AuthenticationPrincipal CustomUserDetails customUser,
                                        @RequestBody RequestUserDTO requestUserDTO){
        String userEmail = customUser.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("사용자 이름 : " + requestUserDTO.getUserName());

        if(requestUserDTO.getUserName() != null) {
            userService.inputUser(userEmail, requestUserDTO.getUserName());
        }

        if(requestUserDTO.getUserContent() != null) {
            makerService.updateMaker(user, requestUserDTO.getUserContent());
        }

        if(requestUserDTO.getUserNick() != null) {
            userService.inputUserNick(userEmail, requestUserDTO.getUserNick());
        }

        return ResponseEntity.ok().build();
    }

    // user 정보 기본 가져오기
    @GetMapping("/get/user")
    public ResponseEntity<?> authUser(@AuthenticationPrincipal CustomUserDetails customUser) {
        String userEmail = customUser.getUsername();
        ResponseUserDTO user = userService.getUserInfo(userEmail);
        return ResponseEntity.ok().body(user);
    }

    // user 정보 ISMS 기준에 따라서 마스킹
    @GetMapping("/get/user/isms")
    public ResponseEntity<?> authUserISMS(@AuthenticationPrincipal CustomUserDetails customUser) {
        String userEmail = customUser.getUsername();
        ResponseUserDTO user = userService.getUserInfoISMS(userEmail);
        return ResponseEntity.ok().body(user);
    }

    // 비밀번호 확인
    @PostMapping("/auth/password")
    public ResponseEntity<?> authPassword(@AuthenticationPrincipal CustomUserDetails customUser,
                                          @RequestBody RequestUserDTO requestUserDTO) {
        String userEmail = customUser.getUsername();
        boolean verifyPassword = userService.authPassword(userEmail, requestUserDTO.getPassword());
        return ResponseEntity.ok().body(verifyPassword);
    }

    @GetMapping("/test")
    public ResponseEntity test() {
        return ResponseEntity.ok().body("Hello World");
    }

//    @GetMapping("/auth/status")
//    public ResponseEntity<Boolean> checkAuthStatus(@AuthenticationPrincipal CustomUserDetails cud) {
//        User user = CommonUtils.getCurrentUser(cud, userService);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
//        }
//        return ResponseEntity.ok().body(true);
//    }
}
