package com.kosa.backend.Member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="Member")
public class MemberEntity implements UserDetails { // UserDetails를 상속받아 인증 객체로 사용한다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", updatable=false)
    private Long id;

    @Column(name="email", nullable=false, unique=true)
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="authority")
    private String authority;

    @Builder
    public MemberEntity(String email, String password, String authority) {
        this.email = email;
        this.password = password;
        this.authority = authority;
    }

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override // 사용자의 id를 반환(고유한 값)
    public String getUsername() {
        return email;
    }

    @Override // 사용자의 pwd를 반환
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override // 패스워드의 만료 여부 반환
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    @Override // 계정 사용이 가능한지 확인하는 로직
    public boolean isEnabled() {
        return true; // true -> 사용 가능
    }
}