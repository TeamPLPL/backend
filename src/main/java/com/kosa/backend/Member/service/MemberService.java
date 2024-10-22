package com.kosa.backend.Member.service;

import com.kosa.backend.Member.dto.MemberDTO;
import com.kosa.backend.Member.entity.MemberEntity;
import com.kosa.backend.Member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(MemberDTO memberDTO) {
        return userRepository.save(MemberEntity.builder()
                .email(memberDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(memberDTO.getPassword()))
                .authority("ROLE_USER")
                .build()).getId();
    }
}
