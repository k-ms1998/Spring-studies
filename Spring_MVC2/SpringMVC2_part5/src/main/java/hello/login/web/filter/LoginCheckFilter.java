package hello.login.web.filter;


import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LoginCheckFilter implements Filter {
    private static final String[] whitelist = {"/", "/member/add", "/login", "/logout", "/css/*"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LoginCheckFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("LoginCheckFilter doFilter");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();
        try{
            log.info("인증 체크 필터 시작{}", requestUri);

            if (checkWhitelist(requestUri)) {
                log.info("인증 체크 로직 실행 {}", requestUri);

                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청 {}", requestUri);

                    //로그인으로 redirect
                    httpResponse.sendRedirect("/login?redirectURL=" + requestUri);
                    return;
                }
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            throw e;
        }finally{
            log.info("인증 체크 필터 종료 {}", requestUri);
        }




    }

    /**
     * 화이트 리스트의 경우 인증 체크 X
     */
    private boolean checkWhitelist (String requestUri){
        return !PatternMatchUtils.simpleMatch(whitelist, requestUri);
    }

    @Override
    public void destroy() {
        log.info("LoginCheckFilter destroy");
    }
}
