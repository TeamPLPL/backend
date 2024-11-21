package com.kosa.backend.user.service;

import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.dto.UserInfoDTO;
import com.kosa.backend.user.dto.requestDTO.RequestUserDTO;
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

    // 사용자 이름 입력 로직
    public String inputUser(String userEmail, String userName) {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.updateUserName(userName);
            userRepository.save(user);
            return user.getEmail();
        }
        return null;
    }

    // 비밀 번호 검증 로직
    public boolean authPassword(String email, String inputPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // matches를 사용하여 입력 비밀번호와 저장된 해시값을 비교
            return bCryptPasswordEncoder.matches(inputPassword, user.getPassword());
        }
        return false; // 사용자 정보가 없으면 false 반환
    }

    // 유저 정보 가져오는 로직
    public ResponseUserDTO getUserInfo(String userEmail) {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent()) {
            return ResponseUserDTO.toEntity(optionalUser.get());
        }
        return null;
    }

    // 유저 정보 ISMS 마스해서킹 가져오는 로직
    public ResponseUserDTO getUserInfoISMS(String userEmail) {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent()) {
            ResponseUserDTO responseUserDTO = ResponseUserDTO.toEntityByISMS(optionalUser.get());
            String userContent = makerRepository.findByUser(optionalUser.get()).get().getUserContent();
            responseUserDTO.updateUserContent(userContent);
            return responseUserDTO;
        }
        return null;
    }
}
