package com.kosa.backend.user.service;

import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.MakerRepository;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .authority("ROLE_USER")
                .build()).getId();

    }
}
