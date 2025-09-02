package com.hazel.jaksim;

import com.hazel.jaksim.member.CustomOAuth2UserService;
import com.hazel.jaksim.member.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    PasswordEncoder EncoderDi(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider() {
            @Override
            protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
                    throws AuthenticationException {
                String rawPassword = authentication.getCredentials().toString();
                System.out.println("입력한 비밀번호: " + rawPassword);

                String encoded = getPasswordEncoder().encode(rawPassword);
                System.out.println("입력한 비밀번호 암호화한 값: " + encoded);
                System.out.println("DB에 저장된 비밀번호: " + userDetails.getPassword());

                super.additionalAuthenticationChecks(userDetails, authentication);
            }
        };

        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(EncoderDi());
        return authProvider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        FilterChain : 모든 유저의 요청과 서버의 응답 사이에 자동으로 실행해주고 싶은 코드 담는 곳
//        http.csrf((csrf) -> csrf.disable());

//        http.csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository())
//                .ignoringRequestMatchers("/login")
//        );

        http.csrf((csrf) -> csrf.disable());
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/common.js",
                        "/styles.css",
                        "/login",
                        "/ws-stomp",
                        "/join",
                        "/joinMember",
                        "/send",
                        "/verify",
                        "/images/**",
                        "/oauth2/**"
                ).permitAll()
                .anyRequest().authenticated()
        );


        http.authenticationProvider(authenticationProvider());


//        폼으로 로그인하기
        http.formLogin((formLogin)
                -> formLogin.loginPage("/login")
                .failureHandler((request, response, exception) -> {
                    if (exception instanceof BadCredentialsException) {
                        response.sendRedirect("/login?error=bad_credentials");
                    } else {
                        response.sendRedirect("/login?error=system_error");
                    }
                })
                .defaultSuccessUrl("/calendar", true) // ← true 주의
        );

        // 소셜 로그인
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService) // OAuth2UserService 등록
                )
                .failureHandler((request, response, exception) -> {
                    response.sendRedirect("/login?error=oauth_error");
                })
                .defaultSuccessUrl("/calendar", true) // 로그인 성공 후 이동
        );


//        로그아웃
        http.logout( logout -> logout.logoutUrl("/logout") );

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
        );

        return http.build();

//        함수 빠르게 만드려면 ()->{}
    }


//    20250529
//    1. 남이 만든 클래스를 수정하고 싶으면 원본을 수정하는 것 보다는 extends로 복사해서 새로 클래스를 만드는 방법도 있습니다.
//
//2. extends로 복사할 때 super를 사용해서 constructor도 복사해서 완전범죄를 꾀하는게 좋습니다.
//
//3. 세션 유효기간 설정도 가능
//
//4. 세션 데이터를 안전하게 DB에 저장도 가능
//
//5. CSRF 기능 켜는 법 배움
//
//6. 라이브러리 세부 문법들은 AI에게 물어보거나 찾아보면 되는 것일 뿐 외울 필요 없습니다.
}

