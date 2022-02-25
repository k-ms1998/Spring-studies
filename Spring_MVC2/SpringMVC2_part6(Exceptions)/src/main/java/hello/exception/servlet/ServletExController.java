package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/error")
public class ServletExController {

    @GetMapping("/ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생!");
    }

    @GetMapping("/404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404 Error!");

    }

    @GetMapping("/400")
    public void error400(HttpServletResponse response) throws IOException {
        response.sendError(400, "404 Error!");

    }

    @GetMapping("/500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500, "500 Error!");

    }
}
