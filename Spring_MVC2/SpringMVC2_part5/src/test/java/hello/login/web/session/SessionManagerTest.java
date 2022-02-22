package hello.login.web.session;

import hello.login.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    @AfterEach
    public void afterEach() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void createSession() {
        Member member = new Member("test1", "userA", "12345");
        sessionManager.createSession(member, response);
        assertThat(response.getCookies().length).isEqualTo(1);
    }

    @Test
    void getSession() {
        Member member = new Member("test1", "userA", "12345");
        sessionManager.createSession(member, response);
        request.setCookies(response.getCookies());

        assertThat(sessionManager.getSession(request)).isEqualTo(member);
    }

    @Test
    void expire() {
        Member member = new Member("test1", "userA", "12345");
        sessionManager.createSession(member, response);
        sessionManager.expire(request);
        assertThat(sessionManager.getSession(request)).isNull();
    }
}