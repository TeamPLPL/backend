package com.kosa.backend.config.service;

import com.kosa.backend.config.dto.GoogleResponse;
import com.kosa.backend.config.dto.NaverResponse;
import com.kosa.backend.config.dto.OAuth2Response;
import com.kosa.backend.user.dto.CustomOAuth2User;
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

        // DefaultOAuth2UserService 생성자를 super를 이용해서 값을 받음
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        // registrationId는 goole, naver, kakao 인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
//        else if(registrationId.equals("kakao")){
//            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
//        }
        else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬 -> User Entity에서 provider가 아이디값
        //ex) 네이버 : naver_xxxx..., 구글 : google_xxxx...
        String provider = oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();

        Optional<User> optionalUser = userRepository.findByProvider(provider);

        // provider가 존재하지 않을 경우 소셜 회원 가입
        if(!optionalUser.isPresent()){
            // user 생성
            int id = userRepository.save(User.builder()
                    .email(oAuth2Response.getEmail())
                    .userName(oAuth2Response.getName())
                    .authority(Authority.ROLE_USER)
                    .joinDate(LocalDateTime.now())
                    .isQuit(false)
                    .complaintCount(0)
                    .provider(provider)
                    .build()).getId();

            // maker 생성
            makerRepository.save(Maker.builder()
                    .user(userRepository.findById(id).get())
                    .userContent("자유롭게 자기소개를 입력하세요.")
                    .build());

            UserOAuthDTO userOAuthDTO = new UserOAuthDTO();
            userOAuthDTO.setProvider(provider);
            userOAuthDTO.setUserName(oAuth2Response.getName());
            userOAuthDTO.setAuthority(Authority.ROLE_USER);

            return new CustomOAuth2User(userOAuthDTO);
        } else {
            User user = optionalUser.get();
            user.setEmail(oAuth2Response.getEmail());
            user.setUserName(oAuth2Response.getName());

            UserOAuthDTO userOAuthDTO = new UserOAuthDTO();
            userOAuthDTO.setProvider(provider);
            userOAuthDTO.setUserName(oAuth2Response.getName());
            userOAuthDTO.setAuthority(Authority.ROLE_USER);

            return new CustomOAuth2User(userOAuthDTO);
        }
    }
}
