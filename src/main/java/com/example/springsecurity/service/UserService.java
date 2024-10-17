package com.example.springsecurity.service;

import com.example.springsecurity.dto.AddUserRequest;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest addUserRequest) {
        if(userRepository.existByEmail(addUserRequest.getEmail())) {
            return userRepository.save(User.builder()
                    .email(addUserRequest.getEmail())
                    .password(bCryptPasswordEncoder.encode(addUserRequest.getPassword()))
                    .build()).getId();
        } else {
            return null;
        }
    }
}
