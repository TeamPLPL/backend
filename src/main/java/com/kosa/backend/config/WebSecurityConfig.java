package com.kosa.backend.config;

import com.kosa.backend.user.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/*
       이전 버전 : extends WebSecurityConfigureAdapter 상속받음, anthMatchers("/") 사용
       이후 버전 : 상속 대신 @Bean등록, requestMatchers("/") 사용
       스프링 3.1.x 이후 (스프링 6.1.x) : 무조건 람다, 열겨식으로 쓰지 말것. -> .and() 쓰지 말것
*/
@Configuration          // Configuration : Spring 설정 에너테이션
@EnableWebSecurity      // EnableWebSecurity : Spring Security 사용(무조건 써야함)
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserDetailService userDetailService;

    /*
           스프링 시큐리티의 모든 기능을 사용하지 않게 설정하는 코드
           일반적으로 정적 리소스(이미지, HTML파일)에 설정한다.
     */
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChin(HttpSecurity http) throws Exception {
        /*
            csrf는 token을 갖이 보내고 해야하기 때문에 여기서는 설정 안함.
        */
        http
                .csrf((csrfConfig) -> csrfConfig
                        .disable());

        http
            /*
                authorizeHttpRequests : 특정한 경로에 요청 등록거부
                사용법 : ramda식 사용 (authorize) -> authorize.
            */
            .authorizeHttpRequests(authorize -> authorize
                    /*
                        hasRole(String role) : 해당 권한 갖고 있어야 경로 권한 허용.
                        permitAll() : 모든 경로 권한 허용.
                        authenticated() : 권한을 갖고 있어야함. 즉 로그인만 하면 접근할 수 있음.
                        denyAll() : 접근 경로 모두 허용X
                     */
                    .requestMatchers("/api/login", "/api/signup","/api/user").permitAll()
                    /*
                        requestMathcer(String url).hasRole("ADMIN", "USER") 권한 여러개 등록할 수있음.
                    */
                    .requestMatchers("/api/admind/**").hasRole("ADMIN")
                    // 그 외에는 .anyRequest().authenticated()로 로그인 할 경우에만 접근할 수 있음.
                    .anyRequest().authenticated()
            );

        // postman 사용하기 위한 임시 httpBasic
        http
                .httpBasic(withDefaults());

        /*
            custom 설정:
            ! 옛날에는 .and() 쓰고서 한 번에 작성해야 했지만 현재는 deprecated로 와르르 작성하지 않음
            아래와 같이 나뉘어 작성되어 독립적으로 동작함.
        */
        http
                .formLogin(formLogin -> formLogin
                /*
                    loginPage("/login") : custom 로그인 페이지 URL 설정함.
                     spring security는 자체적으로 로그인 페이지를 제공하지만
                     loginPage()를 사용해서 사용자가 정의한 페이지로 설정할 수 있다.
                */
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                /*
                        loginProcessingUrl("/login") : loginForm에 있는 <form action="/login"> url에 연결함.
                        기본적으로는 위와같이 자동으로 설정되어 있어서 쓰지 않아도 되고
                        명시적으로 작성해서 custom할 수 있음.
                        -> form이기 때문에 method="post" 이다.
                 */
                .defaultSuccessUrl("/"))

                .logout(logout -> logout
                        .logoutUrl("/logout")                 // 로그아웃 URL
                        .logoutSuccessUrl("/")                // 로그아웃 성공 후 리다이렉트
                        .invalidateHttpSession(true)          // 세션 무효화
                        .deleteCookies("JSESSIONID")   // 쿠키 삭제
                );


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
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