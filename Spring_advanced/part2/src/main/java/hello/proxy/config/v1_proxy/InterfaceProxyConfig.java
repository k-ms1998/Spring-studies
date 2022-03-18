package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 프록시를 스프링 빈에 등록
 * **프록시와 의존관계 주입을 통해 app.v1 코드들을 변경하지 않고도 LogTrace 기능들을 추가할 수 있음**
 */
@Configuration
public class InterfaceProxyConfig {

    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace) {
        OrderControllerV1Impl controller = new OrderControllerV1Impl(orderService(logTrace));
        return new OrderControllerInterfaceProxy(controller, logTrace);
    }

    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace) {
        OrderServiceV1Impl service = new OrderServiceV1Impl(orderRepository(logTrace));
        return new OrderServiceInterfaceProxy(service, logTrace);
    }

    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
        OrderRepositoryV1Impl repository = new OrderRepositoryV1Impl();
        return new OrderRepositoryInterfaceProxy(repository, logTrace);
    }

}
