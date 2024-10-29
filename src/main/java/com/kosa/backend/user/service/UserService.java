package com.kosa.backend.user.service;

import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.entity.UserEntity;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(UserDTO userDTO) {
        return userRepository.save(UserEntity.builder()
                .email(userDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .authority("ROLE_USER")
                .build()).getId();
    }
}
