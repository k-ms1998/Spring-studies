package study.datajpa.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HelloController {

    @GetMapping("/")
    public HelloRes hello() {
        return new HelloRes("hello");
    }

    @Data
    static class HelloRes {
        private String message;
        private LocalDateTime dateTime;

        public HelloRes(String message) {
            this.message = message;
            this.dateTime = LocalDateTime.now();
        }
    }
}
