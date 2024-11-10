package com.kosa.backend.config;

import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;
import java.util.Collections;

/*
*   작성자 : 신은호, 내용 : CORS 이슈 해결 config 파일
*   .addMapping("/api/**") CORS 허용할 url 작성
*   .allowedOrigins("http://localhost:3000") 프론트엔트 포트번호 작성
*
*   특이사항 : 24-11-09, security 설정으로 WebSecurity에서 CORS 설정, 해당은 MVC CORS 설정
*/
//@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .exposedHeaders("Set-Cookie")
                .allowedOrigins("http://localhost:3000");
    }
}
