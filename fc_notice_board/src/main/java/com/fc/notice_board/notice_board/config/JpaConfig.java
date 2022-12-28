package com.fc.notice_board.notice_board.config;

import com.fc.notice_board.notice_board.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        /**
         * 시큐리티 정보를 가져오고, 인증되었는지 확인하고, 인증되었으면 BoardPrincipal 로 캐시팅한 후, username 반환 
         *  -> Auditing 에서 createdBy, modifiedBy 를 username 으로 설정
         */
        return () -> Optional.ofNullable(SecurityContextHolder.getContext()) // Security 에 대한 모든 정보 가져오기
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal) // Object 반환
                .map(BoardPrincipal.class::cast) // == (principal -> (BoardPrincipal) principal)
                .map(BoardPrincipal::getUsername);
    }

}
