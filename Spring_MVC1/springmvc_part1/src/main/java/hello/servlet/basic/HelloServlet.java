package hello.servlet.basic;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        System.out.println("HelloServlet.service");
        System.out.println("req = " + req);
        System.out.println("res = " + res);

//        http://localhost:8080/hello?user=kim&username=min_seop
        System.out.println(req.getQueryString()); //Output: 'ser=kim&username=min_seop'
        System.out.println("username="+req.getParameter("username")); //Output: 'username=min_seop'

        String username = req.getParameter("username");
        if (username == null) {
            username = "NA";
        }
        res.setContentType("text/plain");
        res.setCharacterEncoding("utf-8");
        res.getWriter().write("Response:"+username);
    }
}
