package hello.proxy.config;

import hello.proxy.app.v1.*;
import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * App V2
 * => 인터페이스 없는 구체 클래스 & 스프링 빈 수동 등록
 */
@Configuration
public class AppV2Config {
    @Bean
    public OrderControllerV2 orderControllerV2() {
        return new OrderControllerV2(orderServiceV2()); //orderServiceV2 메서드 호출 후, 반환 값을 의존관계 주입
    }

    @Bean
    public OrderServiceV2 orderServiceV2() {
        return new OrderServiceV2(orderRepositoryV2()); //orderRepositoryV2 메서드 호출 후 반환 값을 의존관계 주입
    }

    @Bean
    public OrderRepositoryV2 orderRepositoryV2() {
        return new OrderRepositoryV2();
    }
}
