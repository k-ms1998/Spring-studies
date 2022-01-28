package hello.springmvc.basic.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class MappingController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = {"/hello-basic", "/hello-go"}, method = RequestMethod.GET)
    public String helloBasic() {
        log.info("helloBasic");

        return "ok";
    }

    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data) {
//        @PathVariable("userId") String data == @PathVariable String userId
        log.info(" mappingPath userId={}", data);

        return data;
    }

    @GetMapping("/mapping/{userId}/orders/{orderId}")
    public String mappingPath(@PathVariable String userId, @PathVariable int orderId) {
        log.info("mappingPath userId={}, orderId={}", userId, orderId);

        return userId+":"+(orderId+10);
    }

    @GetMapping(value = "/mapping-param", params = "user=hello")
    public String mappingParam() {
//        /mapping-param?user=hello => success
//        /mapping-param?user=hello&order=100 => success
//        /mapping-param || /mapping-param?user=helloB || /mapping-param?user=helloA&order=100 => error

        log.info("mappingParam");

        return "ok";
    }

    @GetMapping(value = "/mapping-header", headers = "user=myHeader")
    public String mappingHeaders() {
//      user-myHeader field must be in headers
        log.info("mappingHeaders");

        return "ok";
    }
}
