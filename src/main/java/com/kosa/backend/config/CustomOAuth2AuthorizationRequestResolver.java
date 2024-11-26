package com.kosa.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        return customizeAuthorizationRequest(authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(authorizationRequest);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest request) {
        if (request == null) {
            return null;
        }

        // registrationId 추출 (OAuth2AuthorizationRequest에서 직접 제공)
        String registrationId = request.getAttributes().get("registration_id").toString();

        // 공급자별 추가 파라미터 설정
        return OAuth2AuthorizationRequest.from(request)
                .additionalParameters(params -> {
                    if ("google".equals(registrationId)) {
                        params.put("prompt", "consent"); // Google: 동의창 강제 띄우기
                    } else if ("naver".equals(registrationId)) {
                        params.put("auth_type", "reprompt"); // Naver: 동의창 강제 띄우기
                    }
                })
                .build();
    }

}