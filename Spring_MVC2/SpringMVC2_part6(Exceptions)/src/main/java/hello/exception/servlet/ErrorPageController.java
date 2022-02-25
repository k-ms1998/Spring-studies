package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

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

    //같은 "/error/500" 에 대한 request여도,
    // request시, Header에서 Accept 필드의 값이 application/json 이면,
    // produces의 값과 Accept 필드의 값이 일치하는 request가 우선 순위를 갖는다
    // => 충돌 X
    @RequestMapping(value = "/500", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> errorPage500Api(
            HttpServletRequest request, HttpServletResponse response
    ) {
        log.info("API errorPage500");

        Map<String, Object> result = new HashMap<>();
        Exception ex = (Exception) request.getAttribute(ERROR_EXCEPTION);
        result.put("status", request.getAttribute(ERROR_STATUS_CODE));
        result.put("message", ex.getMessage());

        Integer statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);

        return new ResponseEntity<>(result, HttpStatus.valueOf(statusCode));
    }
}
