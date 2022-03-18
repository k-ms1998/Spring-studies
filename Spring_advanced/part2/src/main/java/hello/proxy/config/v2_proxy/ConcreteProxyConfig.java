package hello.proxy.config.v2_proxy;

import hello.proxy.app.v1.*;
import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.config.v2_proxy.concrete_proxy.OrderControllerConcreteProxy;
import hello.proxy.config.v2_proxy.concrete_proxy.OrderRepositoryConcreteProxy;
import hello.proxy.config.v2_proxy.concrete_proxy.OrderServiceConcreteProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 클래스 기반 프록시는 상속을 사용하기 때문에 몇가지 제약이 있다.
 * 1. 부모 클래스의 생성자를 호출해야 한다.(앞서 본 예제)
 * 2. 클래스에 final 키워드가 붙으면 상속이 불가능하다.
 * 3. 메서드에 final 키워드가 붙으면 해당 메서드를 오버라이딩 할 수 없다.
 * => 인터페이스 기반의 프록시가 더 선호됨. But, 경우에 따라서 클래스 기반의 프록시가 필요로 할때가 있음
 */
@Configuration
public class ConcreteProxyConfig {
    @Bean
    public OrderControllerV2 orderControllerV2(LogTrace logTrace) {
        OrderControllerV2 controller = new OrderControllerV2(orderServiceV2(logTrace));
        return new OrderControllerConcreteProxy(controller, logTrace);
    }

    @Bean
    public OrderServiceV2 orderServiceV2(LogTrace logTrace) {
        OrderServiceV2 service = new OrderServiceV2(orderRepositoryV2(logTrace));
        return new OrderServiceConcreteProxy(service, logTrace);
    }

    @Bean
    public OrderRepositoryV2 orderRepositoryV2(LogTrace logTrace) {
        OrderRepositoryV2 repository = new OrderRepositoryV2();
        return new OrderRepositoryConcreteProxy(repository, logTrace);
    }

}
