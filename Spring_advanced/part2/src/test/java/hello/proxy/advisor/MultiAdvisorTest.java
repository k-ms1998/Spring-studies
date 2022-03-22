package hello.proxy.advisor;

import hello.proxy.common.ServiceImpl;
import hello.proxy.common.ServiceInterface;
import hello.proxy.common.advice.TimeAdvice;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class MultiAdvisorTest {

    @Test
    @DisplayName("여러 프록시")
    void multiAdvisorTest1() {
        /**
         * client -> proxy2(advisor2) -> proxy1(advisor1) -> target
         */

        //프록시1 생성; 프록시1은 target을 의존하도록 생성
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        proxyFactory1.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();

        //프록시2 생성; 프록시2는 프록시1을 의존하도록 생성
        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        proxyFactory2.addAdvisor(advisor2);
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();


        //실행
        proxy2.save();
        /**
         * 결과:
         * Advice2 호출
         * Advice1 호출
         * save 호출
         */

        /**
         *  방법이 잘못된 것은 아니지만, 프록시를 2번 생성해야 한다는 문제가 있다.
         *  만약 적용해야 하는 어드바이저가 10개라면 10개의 프록시를 생성해야한다
         */

    }


    @Test
    @DisplayName("하나의 프록시, 여러 어드바이저")
    void multiAdvisorTest2() {
        /**
         * client -> proxy -> advisor2 -> advisor1 -> target
         */

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("save*");

        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(pointcut, new Advice1());
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(pointcut, new Advice2());

        //프록시1 생성; 프록시1은 target을 의존하도록 생성
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        /**
         * 호출하길 바라는 순서대로 어디바이저 추가
         * addAdvisor 하는 순서 중요!
         * 등록하는 순서대로 advisor 호출됨
         */
        proxyFactory.addAdvisor(advisor2);
        proxyFactory.addAdvisor(advisor1);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        //실행
        proxy.save();
        proxy.find();
        /**
         * 결과:
         * Advice2 호출
         * Advice1 호출
         * save 호출
         * find 호출
         */


    }

    @Slf4j
    static class Advice1 implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("Advice1 호출");
            return invocation.proceed();
        }
    }

    @Slf4j
    static class Advice2 implements MethodInterceptor{

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("Advice2 호출");
            return invocation.proceed();
        }
    }
    
    
    
}
