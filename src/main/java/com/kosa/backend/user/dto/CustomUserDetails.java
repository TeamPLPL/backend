package com.kosa.backend.user.dto;

import com.kosa.backend.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user; // 일반 사용자
    private final UserOAuthDTO userOAuthDTO; // OAuth2 사용자

    // 일반 사용자용 생성자
    public CustomUserDetails(User user) {
        this.user = user;
        this.userOAuthDTO = null; // OAuth2 사용자가 아니면 null
    }

    // OAuth2 사용자용 생성자
    public CustomUserDetails(UserOAuthDTO userOAuthDTO) {
        this.user = null; // 일반 사용자가 아니면 null
        this.userOAuthDTO = userOAuthDTO;
    }

    // 권한 처리
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        if (user != null) {
            // 일반 사용자 권한
            collection.add(() -> user.getAuthority().toString());
        } else if (userOAuthDTO != null) {
            // OAuth2 사용자 권한
            collection.add(() -> userOAuthDTO.getAuthority().name());
        }

        return collection;
    }

    // OAuth2User 메서드: OAuth2 사용자 속성 반환
    @Override
    public Map<String, Object> getAttributes() {
        // userOAuthDTO가 null이면 빈 Map 반환
        return null;
    }

    // OAuth2User 메서드: OAuth2 사용자 이름 반환
    @Override
    public String getName() {
        return userOAuthDTO != null ? userOAuthDTO.getUserName() : null;
    }

    // 사용자 이메일 반환 (OAuth2와 일반 사용자 모두 지원)
    public String getEmail() {
        if (user != null) {
            return user.getEmail();
        } else if (userOAuthDTO != null) {
            return userOAuthDTO.getEmail();
        }
        return null;
    }

    // OAuth2 제공자 정보 반환
    public String getProvider() {
        return userOAuthDTO != null ? userOAuthDTO.getProvider() : null;
    }

    // UserDetails 메서드: 일반 사용자 비밀번호 반환
    @Override
    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }

    // UserDetails 메서드: 일반 사용자 이름 반환
    @Override
    public String getUsername() {
        return user != null ? user.getEmail() : null;
    }

    // UserDetails 메서드: 계정 상태 처리 (기본값 true)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

