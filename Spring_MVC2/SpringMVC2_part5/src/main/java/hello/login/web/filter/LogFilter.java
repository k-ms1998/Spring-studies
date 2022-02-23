package hello.login.web.filter;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LogFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("LogFilter doFilter");
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String uuid = UUID.randomUUID().toString();
        String reqUri = httpRequest.getRequestURI();

        try {
            log.info("REQUEST [{}][{}]", uuid, reqUri);
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally{
            log.info("RESPONSE [{}][{}]", uuid, reqUri);
        }


    }

    @Override
    public void destroy() {
        log.info("LogFilter destroy");
    }
}
