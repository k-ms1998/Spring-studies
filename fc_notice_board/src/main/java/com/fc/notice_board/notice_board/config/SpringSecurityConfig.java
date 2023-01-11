package com.fc.notice_board.notice_board.config;

import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.security.BoardPrincipal;
import com.fc.notice_board.notice_board.dto.security.KakaoOAuth2Response;
import com.fc.notice_board.notice_board.repository.UserAccountRepository;
import com.fc.notice_board.notice_board.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

import static org.springframework.security.config.Customizer.*;

@Configuration
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
            ) throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .build();
    }

    /**
     * DB를 접근해서 유저 정보를 가져오는 메서드
     */
    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("Could Not Find Username."));
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());

            String registrationId = userRequest.getClientRegistration()
                    .getRegistrationId();  // application.yml 에서 설정한 값으로 들어감 -> "kakao"; security.oauth2.client.registration.kakao
            String providerId = String.valueOf(kakaoResponse.getId());
            /*
             * 카카오로 회원가입 및 로그인시, 따로 username 을 입력 하지 않기 때문에 자체적으로 username 생성해서 저장해줌
             */
            String username = registrationId + "_" + providerId; // "kakao_{Long}"

            /*
            카카오를 통해 로그인할 시, 우리의 자체적인 DB에 비밀번호 필요 X (애초에 카카오 id + 비밀 번호를 통해 로그인을 하기 때문에)
            하지만, UserAccount 의 스펙 상, userPassword 가 nullable = false 이기 때문에 가짜 비밀번호(dummyPassword) 생성해서 저장
             */
            String dummyPassword = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());

            /**
             * 1. 해당 username 을 가진 회원이 존재하는지 확인
             * 2. 있으면, BoardPrincipal 로 변환해서 반환
             * 3. 없으면, UserAccount 생성 후, BoardPrincipal 로 변환 후 반환
             */
            return userAccountService.searchUser(username)
                    .map(BoardPrincipal::from)
                    .orElseGet(() ->
                            BoardPrincipal.from(
                                    userAccountService.saveUser(
                                            username,
                                            dummyPassword,
                                            kakaoResponse.email(),
                                            kakaoResponse.nickname(),
                                            null
                                    )
                            )
                    );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
