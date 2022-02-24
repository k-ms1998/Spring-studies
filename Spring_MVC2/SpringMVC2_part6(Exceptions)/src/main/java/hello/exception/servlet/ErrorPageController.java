package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/error-page-controller")
public class ErrorPageController {


    @RequestMapping("/404") //GET이던 POST이던 오류 발생시 같은 오류 페이지를 보여주면 되므로, @RequestMapping 사용
    public String errorPage404(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 404");

        return "/error-page/404";
    }

    @RequestMapping("/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 500");

        return "/error-page/500";
    }


}
