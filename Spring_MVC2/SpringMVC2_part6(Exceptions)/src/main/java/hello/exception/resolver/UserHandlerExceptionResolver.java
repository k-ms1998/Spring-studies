package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.myException.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try{
            if (ex instanceof UserException) {
                log.info("UserException resolver to 400");

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                String acceptHeader = request.getHeader("Accept");
                if (acceptHeader.equals("application/json")) {
                    Map<String, Object> errorResult = new HashMap<>();

                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());

                    String errorResultString = objectMapper.writeValueAsString(errorResult); // Convert JSON -> String

                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(errorResultString);

                    return new ModelAndView();
                } else if (acceptHeader.equals("text/html")) {

                    return new ModelAndView("error/400"); //View error/400.html 랜더링
                }else{

                    return new ModelAndView();
                }


            }
        }catch(Exception e){
            log.info("resolver ex", e);
        }

        return null;
    }

}
