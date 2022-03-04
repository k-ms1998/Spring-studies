package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/hello-v1")
    public String helloV1(HttpServletRequest reqeust) {
        String data = reqeust.getParameter("data");
        Integer intValue = Integer.valueOf(data);

        log.info("intValue={}", intValue);

        return "ok";
    }

    @GetMapping("/hello-v2") // localhost:8080/hello-v2?data1=10 => data == "10"
    public String helloV2(@RequestParam Integer data) {
//        registry에 StringToIntegerConverter 추가시 알아서 String => Integer로 변환 해줌

        log.info("data={}", data);

        return "ok";
    }

    @GetMapping("/ip-port") //localhost:8080/ip-port?ipPort=127.0.0.1:8080 => ipPort == "127.0.0.1:8080"
    public String ipPort(@RequestParam IpPort ipPort) {
        //registry에 StringToIpPortConverter 등록 => ipPort String => IpPort로 변환

        log.info("IpPort Ip={}", ipPort.getIp());
        log.info("IpPort Port={}", ipPort.getPort());

        return "ok";
    }
}
