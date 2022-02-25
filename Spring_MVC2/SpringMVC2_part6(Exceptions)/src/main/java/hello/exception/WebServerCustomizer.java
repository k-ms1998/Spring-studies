package hello.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

//@Component
// 스프링 부트에서 기본으로 BasicErrorController를 등록해주기 때문에, BasicErrorController를 사용하면 됨 => WebServerCustomizer 필요 X
// BasicErrorController는 기본으로 등록되어 있기 때문에 별도로 Controller를 생성할 필요 X
public class WebServerCustomizer
        implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page-controller/404"); //404 Error 발생 시 => path로 reqeust 보냄
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page-controller/500"); //500 Error 발생 시 => path로 request 보냄
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page-controller/500"); //Run Time Exception 발생 시 => path로 request 보냄


        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
        //오류 페이지들 등록; 오류 발생 => 오류 코드 확인 => 등록된 오류 페이지 중, 해당 하는 오류 코드의 path로 request 보냄 => 오류 페이지 랜더링
    }
}
