package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션 관리
 */
@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성
     * 1.sessionId 생성 (임의의 추정 불가능한 랜덤 값)
     * 2.세션 저장소에 sessionId와 보관할 값 저장
     * 3.sessionId로 응답 쿠키를 생성해서 클라이언트에 전달
     */
    public void createSession(Object value, HttpServletResponse res) {
        //세션 id 생성 & 값을 세선에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);
        /**
         * sessionStore<UUID, Member>
         */

        //쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        res.addCookie(mySessionCookie);
        /**
         * Cookie: "mySessionId"=UUID
         */

    }

    /**
     * 세션 조회
     */
    public Object getSession(HttpServletRequest req) {
        Cookie sessionCookie = findCookie(req);
        if (sessionCookie == null) {
            return null;
        }
        /**
         * sessionCookie=<"mySessionId", UUID>
         * sessionStore=<UUID, Member>
         * sessionCookie.getValue() = UUID
         * sessionStore.get(UUID) = Member
         * => return Member
         */

        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션 만료(삭제)
     */
    public void expire(HttpServletRequest req) {
        Cookie sessionCookie = findCookie(req);
        if (sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue());
        }
    }

    public Cookie findCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(SESSION_COOKIE_NAME))
                .findAny()
                .orElse(null);

    }

}
