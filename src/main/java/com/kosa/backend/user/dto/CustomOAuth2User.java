package com.kosa.backend.user.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class CustomOAuth2User implements OAuth2User {
    private final UserOAuthDTO userOAuthDTO;

    public CustomOAuth2User(UserOAuthDTO userOAuthDTO){
        this.userOAuthDTO =userOAuthDTO;
    }

    // 기본적으로 제공하지만, naver/google 달라서 따로 만든다.
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                // name() 메서드는 Enum 상수의 이름을 그대로 String으로 반환
                return userOAuthDTO.getAuthority().name();
            }
        });
        return collection;
    }

    // 원래 return userDTO.getName();
    @Override
    public String getName() {
        return userOAuthDTO.getUserName();
    }

    //
    public String getEmail() { return userOAuthDTO.getEmail(); }

    public String getProvider() {
        return userOAuthDTO.getProvider();
    }
}
