package hello.proxy.config.proxyFactory;

import hello.proxy.app.v1.*;
import hello.proxy.config.proxyFactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProxyFactoryConfigV1 {


    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace) {
        OrderControllerV1 controller = new OrderControllerV1Impl(orderService(logTrace));
        ProxyFactory proxyFactory = new ProxyFactory(controller);
        proxyFactory.addAdvisor(getAdvisor(logTrace));
        OrderControllerV1 proxy = (OrderControllerV1) proxyFactory.getProxy();

        return proxy;
    }


    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace) {
        OrderServiceV1Impl service = new OrderServiceV1Impl(orderRepository(logTrace));
        ProxyFactory proxyFactory = new ProxyFactory(service);
        proxyFactory.addAdvisor(getAdvisor(logTrace));
        OrderServiceV1 proxy = (OrderServiceV1) proxyFactory.getProxy();

        return proxy;
    }


    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
        OrderRepositoryV1 repository = new OrderRepositoryV1Impl();
        ProxyFactory proxyFactory = new ProxyFactory(repository);
        proxyFactory.addAdvisor(getAdvisor(logTrace));
        OrderRepositoryV1 proxy = (OrderRepositoryV1) proxyFactory.getProxy();

        return proxy;
    }

    private Advisor getAdvisor(LogTrace logTrace) {
        //Pointcut => 필터링되는 조건
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*"); //no-log일때는 로그가 출력이 되지 않도록 필터링하지 않을 메서드 이름들 설정해줌

        //Advice 생성 => 실질적인 부가기능을 담은 구현체
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);

        //Advisor 생성 => advisor = pointcut + advice
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);

        return advisor;
    }


}
