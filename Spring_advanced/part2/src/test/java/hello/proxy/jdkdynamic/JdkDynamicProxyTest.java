package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.AImpl;
import hello.proxy.jdkdynamic.code.BImpl;
import hello.proxy.jdkdynamic.code.InterfaceA;
import hello.proxy.jdkdynamic.code.InterfaceB;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

@Slf4j
public class JdkDynamicProxyTest {
    /**
     * 1. 클라이언트는 JDK 동적 프록시의 call() 을 실행한다.
     * 2. JDK 동적 프록시는 InvocationHandler.invoke() 를 호출한다. TimeInvocationHandler 가 구현체로 있으로 TimeInvocationHandler.invoke() 가 호출된다.
     * 3. TimeInvocationHandler 가 내부 로직을 수행하고, method.invoke(target, args) 를 호출해서 target 인 실제 객체(AImpl)를 호출한다.
     * 4. AImpl 인스턴스의 call() 이 실행된다.
     * 5. AImpl 인스턴스의 call() 의 실행이 끝나면 TimeInvocationHandler 로 응답이 돌아온다. 시간 로그를 출력하고 결과를 반환한다.
     */

    @Test
    void dynamicA() {
        InterfaceA target = new AImpl();
        TimeInvocationHandler invocationHandler = new TimeInvocationHandler(target);

        //프록시 생성
        InterfaceA proxy = (InterfaceA) Proxy.newProxyInstance(InterfaceA.class.getClassLoader(), new Class[]{InterfaceA.class}, invocationHandler); // proxy.call()하면 자동으로 invocationHandler의 invoke() 호출됨

        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());


    }

    @Test
    void dynamicB() {
        InterfaceB target = new BImpl();
        TimeInvocationHandler invocationHandler = new TimeInvocationHandler(target);

        //프록시 생성
        InterfaceB proxy = (InterfaceB) Proxy.newProxyInstance(InterfaceB.class.getClassLoader(), new Class[]{InterfaceB.class}, invocationHandler); // proxy.call()하면 자동으로 invocationHandler의 invoke() 호출됨

        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());


    }
}
