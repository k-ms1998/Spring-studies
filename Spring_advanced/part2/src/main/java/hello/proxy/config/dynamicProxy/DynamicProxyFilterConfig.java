package hello.proxy.config.dynamicProxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.dynamicProxy.handler.LogTraceFilterHandler;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
public class DynamicProxyFilterConfig {
    /**
     * LogTraceFilterHandler : 동적 프록시를 만들더라도 LogTrace 를 출력하는 로직은 모두 같기 때문에
     * 프록시는 모두 LogTraceFilterHandler 를 사용한다
     */
    private static final String[] PATTERNS = {"request*", "order*", "save*"}; //request, order, save로 시작하면 로그를 남긴다

    @Bean
    public OrderControllerV1 orderCOntrollerV1(LogTrace logTrace) {
        OrderControllerV1 orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));

        OrderControllerV1 proxy = (OrderControllerV1) Proxy.newProxyInstance(OrderControllerV1.class.getClassLoader(),
                new Class[]{OrderControllerV1.class}, new LogTraceFilterHandler(orderController, logTrace, PATTERNS));

        return proxy;
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));

        OrderServiceV1 proxy = (OrderServiceV1) Proxy.newProxyInstance(OrderServiceV1.class.getClassLoader(),
                new Class[]{OrderServiceV1.class}, new LogTraceFilterHandler(orderService, logTrace, PATTERNS));

        return proxy;
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();

        OrderRepositoryV1 proxy = (OrderRepositoryV1) Proxy.newProxyInstance(OrderRepositoryV1.class.getClassLoader(),
                new Class[]{OrderRepositoryV1.class}, new LogTraceFilterHandler(orderRepository, logTrace, PATTERNS));

        return proxy;
    }
}
