package com.fc.notice_board.notice_board.config;

import com.fc.notice_board.notice_board.domain.UserAccount;
import com.fc.notice_board.notice_board.repository.UserAccountRepository;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@Import(SpringSecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserAccountRepository userAccountRepository;

    @BeforeTestMethod
    public void securitySetup() {
        given(userAccountRepository.findById(any(String.class))).willReturn(Optional.of(
                UserAccount.of(
                        "kmsTest",
                        "password",
                        "email",
                        "nickname",
                        "memo",
                        "kms-test",
                        "kms-test"
                )
        ));
    }
}
