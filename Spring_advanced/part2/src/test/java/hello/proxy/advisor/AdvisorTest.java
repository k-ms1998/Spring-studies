package hello.proxy.advisor;

import hello.proxy.common.ServiceImpl;
import hello.proxy.common.ServiceInterface;
import hello.proxy.common.advice.TimeAdvice;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

@Slf4j
public class AdvisorTest {

    @Test
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        /**
         * DefaultPointcutAdvisor(Pointcut pointcut, Advice advice)
         * Advisor 인터페이스의 가장 일반적인 구현체이다.
         * 생성자를 통해 하나의 포인트컷과 하나의 어드바이스를 넣어주면 된다. 어드바이저는 하나의 포인트컷과 하나의 어드바이스로 구성된다
         */
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());

        /**
         * addAdvisor(Advisor advisor)
         * 프록시 팩토리에 적용할 어드바이저를 지정한다.
         * 어드바이저는 내부에 포인트컷과 어드바이스를 모두 가지고 있다.
         * 따라서 어디에 어떤 부가 기능을 적용해야 할지 어드바이스 하나로 알 수 있다.
         * 프록시 팩토리를 사용할 때 어드바이저는 필수이다.
         */
        proxyFactory.addAdvisor(advisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.find();
        proxy.save();

    }

    @Test
    @DisplayName("직접 만든 포인트컷")
    void advisorTest2() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        //DefaultPointCutAdvisor(Pointcut pointcut, Advice advice)
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointCut(), new TimeAdvice());

        proxyFactory.addAdvisor(advisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        /**
         * 실행 결과를 보면 기대한 것과 같이 save() 를 호출할 때는 어드바이스가 적용되지만,
         * find() 를 호출할 때는 어드바이스가 적용되지 않는다
         * => save()일때는 부가 기능(TimeAdvice) 적용 O
         * => find()일때는 부가 기능(TimeAdvice) 적용 X
         */
        proxy.find();
        proxy.save();

    }

    @Test
    @DisplayName("스프링이 제공하는 포인트컷")
    void advisorTest3() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        /**
         * NameMatchMethodPointcut : 메서드 이름을 기반으로 매칭한다. 내부에서는 PatternMatchUtils 를사용한다.
         * => *save*, *save, save* 등 허용
         */
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("save*"); //메서드의 이름이 'save'인 경우에만 proxy 호출

        //DefaultPointCutAdvisor(Pointcut pointcut, Advice advice)
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());

        proxyFactory.addAdvisor(advisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        /**
         * 실행 결과를 보면 기대한 것과 같이 save() 를 호출할 때는 어드바이스가 적용되지만,
         * find() 를 호출할 때는 어드바이스가 적용되지 않는다
         * => save()일때는 부가 기능(TimeAdvice) 적용 O
         * => find()일때는 부가 기능(TimeAdvice) 적용 X
         */
        proxy.find();
        proxy.save();

    }

    /**
     * 직접 구현한 포인트컷이다. Pointcut 인터페이스를 구현한다.
     * 현재 메서드 기준으로 로직을 적용하면 된다. 클래스 필터는 항상 true 를 반환하도록 했고, 메서드 비교
     * 기능은 MyMethodMatcher 를 사용한다.
     */
    static class MyPointCut implements Pointcut {

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }


    /**
     * 직접 구현한 MethodMatcher 이다. MethodMatcher 인터페이스를 구현한다.
     * matches(Method method, Class</?> targetClass) :
     * 이 메서드에 method , targetClass 정보가 넘어온다.
     * 이 정보로 어드바이스를 적용할지 적용하지 않을지 판단할 수 있다
     */
    static class MyMethodMatcher implements MethodMatcher{

        private String MATCHNAME = "save";

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            boolean result = method.getName().equals(MATCHNAME);
            log.info("포인트컷 호출 method={} targetClass={}", method.getName(), targetClass);
            log.info("result={}", result);
            return result;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }

}
