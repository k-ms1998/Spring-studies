package hello.proxy.config.dynamicProxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.dynamicProxy.handler.LogTraceBasicHandler;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

/**
 * JDK 동적 프록시는 인터페이스가 필수이다.
 * 그렇다면 V2 애플리케이션 처럼 인터페이스 없이 클래스만 있는 경우에는 어떻게 동적 프록시를 적용할 수 있을까?
 * 이것은 일반적인 방법으로는 어렵고 CGLIB 라는 바이트코드를 조작하는 특별한 라이브러리를 사용해야 한다.
 */
@Configuration
public class DynamicProxyBasicConfig {
    /**
     * LogTraceBasicHandler : 동적 프록시를 만들더라도 LogTrace 를 출력하는 로직은 모두 같기 때문에
     * 프록시는 모두 LogTraceBasicHandler 를 사용한다
     */

    @Bean
    public OrderControllerV1 orderCOntrollerV1(LogTrace logTrace) {
        OrderControllerV1 orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));

        OrderControllerV1 proxy = (OrderControllerV1) Proxy.newProxyInstance(OrderControllerV1.class.getClassLoader(),
                new Class[]{OrderControllerV1.class}, new LogTraceBasicHandler(orderController, logTrace));

        return proxy;
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));

        OrderServiceV1 proxy = (OrderServiceV1) Proxy.newProxyInstance(OrderServiceV1.class.getClassLoader(),
                new Class[]{OrderServiceV1.class}, new LogTraceBasicHandler(orderService, logTrace));

        return proxy;
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();

        OrderRepositoryV1 proxy = (OrderRepositoryV1) Proxy.newProxyInstance(OrderRepositoryV1.class.getClassLoader(),
                new Class[]{OrderRepositoryV1.class}, new LogTraceBasicHandler(orderRepository, logTrace));

        return proxy;
    }
}
