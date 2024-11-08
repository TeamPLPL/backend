package com.kosa.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
*   작성자 : 신은호, 내용 : CORS 이슈 해결 config 파일
*   .addMapping("/api/**") CORS 허용할 url 작성
*   .allowedOrigins("http://localhost:3000") 프론트엔트 포트번호 작성
*/
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
