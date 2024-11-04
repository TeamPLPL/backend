package com.kosa.backend.user.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.user.entity.enums.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="User")
public class User extends Auditable implements UserDetails { // UserDetails를 상속받아 인증 객체로 사용한다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", updatable=false)
    private int id;

    @Column(name="email", nullable=false, unique=true)
    private String email;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String userNick;

    @Column(name="password")
    private String password;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @Column(nullable = false) // 탈퇴 여부
    private boolean isQuit;

    @Column(nullable = false) // 신고 회수
    private int complaintCount;

    private String provider;

    @Column(name="authority")
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public User(String email, String password, Authority authority, String userNick
        ,String userName,LocalDateTime joinDate,boolean isQuit, int complaintCount) {
        this.email = email;
        this.password = password;
        this.userNick = userNick;
        this.authority = authority;
        this.userName = userName;
        this.joinDate = joinDate;
        this.isQuit = isQuit;
        this.complaintCount = complaintCount;
    }

    @Override // 사용자가 가지는 권한에 대한 정보 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 관련 로직을 여기서 구현하거나, 필요시 Authority와 연결된 GrantedAuthority 리스트를 반환합니다.
        return List.of(() -> authority.name());
    }

    @Override // 인증에 필요한 아이디와 같은 정보, 사용자의 id를 반환(고유한 값)
    public String getUsername() {
        return email;
    }

    @Override // 인증을 마무리하기 위한 패스워드 정보, 사용자의 pwd를 반환
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
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
