package com.kosa.backend.user.service;

import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.entity.enums.Authority;
import com.kosa.backend.user.repository.MakerRepository;
import com.kosa.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    // user 회원가입, 회원가입하면 maker id 자동 생성
    public int save(UserDTO userDTO) {
        int id = userRepository.save(User.builder()
                .email(userDTO.getEmail())
                .userName("유저")
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .authority(Authority.ROLE_USER)
                .userNick(userDTO.getUserNick())
                .joinDate(LocalDateTime.now())
                .isQuit(false)
                .complaintCount(0)
                .build()).getId();

        makerRepository.save(Maker.builder()
                        .user(userRepository.findById(id).get())
                        .userContent("자유롭게 자기소개를 입력하세요.")
                .build());

        return id;
    }

    // email로 해당 User 조회
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }
}
