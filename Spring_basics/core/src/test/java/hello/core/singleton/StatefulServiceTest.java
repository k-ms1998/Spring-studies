package hello.core.singleton;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        //userA가 10000원 주문
        statefulService1.changePrice("userA", 10000);
        //userB기 200000원 주문
        statefulService2.changePrice("userB", 20000);

        //userA가 주문 금액 조회
        System.out.println(statefulService1.getPrice());
        //userA가 주문 금액을 조회하기 전에 userB가 주문을 했으므로, userA의 주문 금액도 변하게 됨
        
        //userB가 주문 금액 조회
        System.out.println(statefulService2.getPrice());

    }

    static class TestConfig{
        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }


}