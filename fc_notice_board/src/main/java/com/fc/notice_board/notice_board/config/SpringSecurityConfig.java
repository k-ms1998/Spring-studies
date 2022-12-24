package com.fc.notice_board.notice_board.config;

import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.security.BoardPrincipal;
import com.fc.notice_board.notice_board.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth ->
                        auth.mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles/",
                                "/articles/search-hashtag"
                        ).permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin().and()
                .build();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        /**
         * ignoring(): 아예 Spring Security 의 검사 대상이 아님
         * (vs. SecurityFilterChain 에서의 permitAll(): Spring Security 가 인증을 혀용해주는 것)
         *  => static resource(css, js 등)은 Spring Security 가 검사를 할 필요할 필요가 없기 때문에 아예 검색에서 제외 
         */
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // toCommonLocations 가 포함하는 위치들은 해당 메서드를 들어가서 확인하면 됨
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return username -> userAccountRepository
                .findById(username)
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("Could Not Find Username."));
    }
}
