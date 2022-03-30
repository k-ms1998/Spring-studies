package hello.aop.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class ExampleTest {

    @Autowired
    ExampleService exampleService;
    
    
    @Test
    void test() {
        for (int i = 0; i < 5; i++) {
            log.info("seq={}", i);
            exampleService.request(String.valueOf(i));
        }
    }
}
