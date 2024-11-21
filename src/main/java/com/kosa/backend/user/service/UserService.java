package com.kosa.backend.user.service;

import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.dto.UserInfoDTO;
import com.kosa.backend.user.dto.responsedto.ResponseUserDTO;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.entity.enums.Authority;
import com.kosa.backend.user.repository.MakerRepository;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MakerRepository makerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final S3Service s3Service;

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
    
    public ResponseEntity<UserInfoDTO> getUserInfo(int userId) {

        Optional<User> currentUser = userRepository.findById(userId);
        if (currentUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .id(userId)
                .userNick(currentUser.get().getUserNick())
                .profileImgUrl(s3Service.getProfileImgByUserId(userId).getSignedUrl())
                .build();

        return ResponseEntity.ok(userInfoDTO);
    }

    public User getUser(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        return user.orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }

    // 작성자 : 신은호, 작성 내용 : 사용자 구분 로직
    public User authenticate(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public ResponseUserDTO getUserInfo(String userEmail) {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent()) {
            return ResponseUserDTO.toEntity(optionalUser.get());
        }
        return null;
    }
}
