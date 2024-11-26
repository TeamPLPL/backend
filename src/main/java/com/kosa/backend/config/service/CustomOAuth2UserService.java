package com.kosa.backend.config.service;

import com.kosa.backend.config.dto.GoogleResponse;
import com.kosa.backend.config.dto.NaverResponse;
import com.kosa.backend.config.dto.OAuth2Response;
import com.kosa.backend.user.dto.CustomOAuth2User;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.dto.UserOAuthDTO;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.entity.enums.Authority;
import com.kosa.backend.user.repository.MakerRepository;
import com.kosa.backend.user.repository.UserRepository;
import com.kosa.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final MakerRepository makerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // DefaultOAuth2UserService를 통해 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // registrationId는 google, naver, kakao 등을 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("Unsupported registrationId: " + registrationId);
        }

        // 리소스 서버에서 받은 정보를 기반으로 고유 provider ID 생성
        String provider = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        // 사용자 데이터베이스에서 provider로 사용자 검색
        Optional<User> optionalUser = userRepository.findByProvider(provider);

        User user;
        if (!optionalUser.isPresent()) {
            // 새 사용자 생성
            user = userRepository.save(User.builder()
                    .email(oAuth2Response.getEmail())
                    .userName(oAuth2Response.getName())
                    .authority(Authority.ROLE_USER)
                    .joinDate(LocalDateTime.now())
                    .isQuit(false)
                    .complaintCount(0)
                    .provider(provider)
                    .build());

            // 새 Maker 엔티티 생성
            makerRepository.save(Maker.builder()
                    .user(user)
                    .userContent("자유롭게 자기소개를 입력하세요.")
                    .build());
        } else {
            // 기존 사용자 정보 업데이트
            user = optionalUser.get();
            user.setEmail(oAuth2Response.getEmail());
            user.setUserName(oAuth2Response.getName());
        }

        // CustomUserDetails 객체 생성
        return new CustomUserDetails(user);
    }
}

