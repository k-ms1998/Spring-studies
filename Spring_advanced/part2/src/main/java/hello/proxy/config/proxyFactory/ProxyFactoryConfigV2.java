package hello.proxy.config.proxyFactory;

import hello.proxy.app.v2.*;
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
public class ProxyFactoryConfigV2 {


    @Bean
    public OrderControllerV2 orderControllerV2(LogTrace logTrace) {
        OrderControllerV2 controller = new OrderControllerV2(orderServiceV2(logTrace));
        ProxyFactory proxyFactory = new ProxyFactory(controller);
        proxyFactory.addAdvisor(getAdvisor(logTrace));
        OrderControllerV2 proxy = (OrderControllerV2) proxyFactory.getProxy();

        return proxy;
    }


    @Bean
    public OrderServiceV2 orderServiceV2(LogTrace logTrace) {
        OrderServiceV2 service = new OrderServiceV2(orderRepositoryV2(logTrace));
        ProxyFactory proxyFactory = new ProxyFactory(service);
        proxyFactory.addAdvisor(getAdvisor(logTrace));
        OrderServiceV2 proxy = (OrderServiceV2) proxyFactory.getProxy();

        return proxy;
    }


    @Bean
    public OrderRepositoryV2 orderRepositoryV2(LogTrace logTrace) {
        OrderRepositoryV2 repository = new OrderRepositoryV2();
        ProxyFactory proxyFactory = new ProxyFactory(repository);
        proxyFactory.addAdvisor(getAdvisor(logTrace));
        OrderRepositoryV2 proxy = (OrderRepositoryV2) proxyFactory.getProxy();

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
