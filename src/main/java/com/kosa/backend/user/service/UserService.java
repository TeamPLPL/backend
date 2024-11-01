package com.kosa.backend.user.service;

import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.entity.enums.Authority;
import com.kosa.backend.user.repository.MakerRepository;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MakerRepository makerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // user 회원가입
    public Long save(UserDTO userDTO) {
        return userRepository.save(User.builder()
                .email(userDTO.getEmail())
                .userName("이름을 변경하세요.")
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .authority(Authority.ROLE_USER)
                .userNick(userDTO.getUserNick())
                .joinDate(LocalDateTime.now())
                .isQuit(false)
                .complaintCount(0)
                .build()).getId();

    }
}
