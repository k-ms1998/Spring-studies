package hello.proxy.config;
import hello.proxy.app.v1.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * App V1
 * => 인터페이스 구현 클래스 & 스프링 빈 수동 등록
 */
@Configuration
public class AppV1Config {
    @Bean
    public OrderControllerV1 orderControllerV1() {
        return new OrderControllerV1Impl(orderServiceV1()); //orderServiceV1 메서드 호출 후 반환되는 값을 의존과계 주입
    }
    @Bean
    public OrderServiceV1 orderServiceV1() {
        return new OrderServiceV1Impl(orderRepositoryV1()); //orderRepositoryV1 메서드 호출 후 반환되는 값을 의존과계 주입
    }
    @Bean
    public OrderRepositoryV1 orderRepositoryV1() {
        return new OrderRepositoryV1Impl();
    }
}