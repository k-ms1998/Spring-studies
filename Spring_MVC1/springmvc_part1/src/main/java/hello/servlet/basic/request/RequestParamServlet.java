package hello.servlet.basic.request;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

/*
* 1.파라미터 전송 기능
* http://localhost:8080/request-param?username=hello&age=20&birth=korea
* http://localhost:8080/request-param?username=hello&age=20&username=hello2
*
* */

@WebServlet(name="requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[전체 파라미터 조회] - Start");
        request.getParameterNames().asIterator()
                .forEachRemaining(e -> {
                    System.out.println(e + " = " + request.getParameter(e));
                });
        System.out.println("[전체 파라미터 조회] - End");

        System.out.println("[단일 파라미터 조회] - Start");
        String username = request.getParameter("username");
        String age = request.getParameter("age");
        System.out.println("username = " + username);
        System.out.println("age = " + age);
        System.out.println("[단일 파라미터 조회] - End");

        System.out.println("[username param 중복] - Start");
        Arrays.stream(request.getParameterValues("username"))
                        .forEach(v -> System.out.println("v = " + v));
        System.out.println("[username param 중복] - End");

    }
}
