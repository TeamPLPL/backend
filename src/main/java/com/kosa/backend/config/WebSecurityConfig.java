package com.kosa.backend.config;

import com.kosa.backend.config.jwt.JWTFilter;
import com.kosa.backend.config.jwt.JWTUtil;
import com.kosa.backend.config.jwt.LoginFilter;
import com.kosa.backend.user.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

/*
   이전 버전 : extends WebSecurityConfigureAdapter 상속받음, anthMatchers("/") 사용
   이후 버전 : 상속 대신 @Bean등록, requestMatchers("/") 사용
   스프링 3.1.x 이후 (스프링 6.1.x) : 무조건 람다, 열겨식으로 쓰지 말것. -> .and() 쓰지 말것
   사용법 : ramda식 사용 (authorize) -> authorize.
*/
@Configuration          // Configuration : Spring 설정 에너테이션
@EnableWebSecurity      // EnableWebSecurity : Spring Security 사용(무조건 써야함)
public class WebSecurityConfig {
    // AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    public WebSecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChin(HttpSecurity http) throws Exception {
        // csrf disable
        http
                .csrf((csrfConfig) -> csrfConfig
                        .disable());
        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        // postman 사용하기 위한 임시 httpBasic
        http
//                .httpBasic(withDefaults());
              .httpBasic((auth) -> auth.disable());

        http
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/api/login", "/api/signup","/api/user").permitAll()
                    // requestMathcer(String url).hasRole("ADMIN", "USER") 권한 여러개 등록할 수있음.
                    .requestMatchers("/api/admind/**").hasRole("ADMIN")
                    // 그 외에는 .anyRequest().authenticated()로 로그인 할 경우에만 접근할 수 있음.
                  .anyRequest().authenticated()
//                    .anyRequest().permitAll() // 개발 중에는 모든 접근 허용
            );

        //JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        //AuthenticationManager()와 JWTUtil 인수 전달
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /*
         스프링 시큐리티는 사용자 인증(로그인)시 비밀번호에 대해 단방향 해시 암호화를 진행하여 저장되어 있는 비밀번호와 대조한다.
         따라서 회원가입시 비밀번호 항목에 대해서 암호화를 진행해야 한다.

         스프링 시큐리티는 암호화를 위해 BCrypt Password Encoder를 제공하고 권장한다.
         따라서 해당 클래스를 return하는 메소드를 만들어 @Bean으로 등록하여 사용하면 된다.
    */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
