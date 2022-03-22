package hello.proxy.proxyfactory;

import hello.proxy.common.ConcreteService;
import hello.proxy.common.ServiceImpl;
import hello.proxy.common.ServiceInterface;
import hello.proxy.common.advice.TimeAdvice;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 대상에 인터페이스가 있으면: JDK 동적 프록시, 인터페이스 기반 프록시
 * 대상에 인터페이스가 없으면: CGLIB, 구체 클래스 기반 프록시
 * proxyTargetClass=true : CGLIB, 구체 클래스 기반 프록시, 인터페이스 여부와 상관없음
 */
@Slf4j
public class ProxyFactoryTest {

    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target); //ProxyFactory를 생성할때 target을 주입 해줌 => TimeAdvice에서 invocation.proceed()할때 target 설정을 안해줘도 됨
                                                            //인스턴스에 인터페이스가 있다면 JDK 동적 프록시를 기본으로 사용하고 인터페이스가 없고 구체 클래스만 있다면 CGLIB를 통해서 동적 프록시를 생성한다
        proxyFactory.addAdvice(new TimeAdvice());   //프록시 팩토리를 통해서 만든 프록시가 사용할 부가 기능 로직을 설정한다

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();    //프록시 객체를 생성하고 그 결과를 받는다.

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.find();
        proxy.save();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(proxy)).isFalse(); //인터페이스를 통한 프록시 이므로, JDKDynamicProxy O && CglibProxy X

    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 사용")
    void concreteProxy() {
        ConcreteService target = new ConcreteService();
        ProxyFactory proxyFactory = new ProxyFactory(target); //ProxyFactory를 생성할때 target을 주입 해줌 => TimeAdvice에서 invocation.proceed()할때 target 설정을 안해줘도 됨

        proxyFactory.addAdvice(new TimeAdvice());   //프록시 팩토리를 통해서 만든 프록시가 사용할 부가 기능 로직을 설정한다

        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();    //프록시 객체를 생성하고 그 결과를 받는다.

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue(); //클래스를 통한 프록시 이므로, JDKDynamicProxy X && CglibProxy O

    }

    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용")
    void proxyTargetClass() {
        /**
         * 인터페이스가 있지만, CGLIB를 사용해서 인터페이스가 아닌 클래스 기반으로 동적 프록시를만드는 방법을 알아보자.
         */
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true); //인터페이스가 있어도 강제로 CGLIB를 사용한다. 그리고 인터페이스가 아닌 클래스 기반의 프록시를 만들어준다
        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();    //프록시 객체를 생성하고 그 결과를 받는다.

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.find();
        proxy.save();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();

    }
}
