package hello.springmvc.basic.response;

import hello.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class ResponseBodyController {

    @GetMapping("response-body-string-v1")
    public void responseBodyStringV1(HttpServletRequest req, HttpServletResponse response) throws IOException {
        response.getWriter().write("ok");
    }

    @GetMapping("response-body-string-v2")
    public ResponseEntity<String> responseBodyStringV2(HttpServletRequest req, HttpServletResponse response) {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK) //@ResponseBody를 써도 status code를 전달해 줌
    @ResponseBody
    @GetMapping("response-body-string-v3")
    public String responseBodyStringV3(HttpServletRequest req, HttpServletResponse response) {
        return "ok";
    }

    @GetMapping("response-json-string-v1")
    public ResponseEntity<HelloData> responseJsonStringV1(){
        HelloData helloData = new HelloData();
        helloData.setUsername("hello");
        helloData.setAge(20);

        return new ResponseEntity<>(helloData, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK) //@ResponseBody를 써도 status code를 전달해 줌
    @ResponseBody
    @GetMapping("response-json-string-v2")
    public HelloData responseJsonStringV2(){
        HelloData helloData = new HelloData();
        helloData.setUsername("hello");
        helloData.setAge(20);

        return helloData;
    }
}

