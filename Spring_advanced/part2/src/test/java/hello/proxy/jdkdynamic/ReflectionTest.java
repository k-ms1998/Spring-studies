package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class ReflectionTest {

    @Test
    void reflection0() {
        /**
         * 호출하는 메서드인 target.callA() , target.callB() 이 부분만 동적으로 처리하는 것이 목표
         * => 리플렉션으로 해결 가능
         */
        Hello target = new Hello();
        
        //공통 로직1 시작
        log.info("start");
        String resultA = target.callA();    //호출하는 메서드만 다름 => 동적으로 처르 필요
        log.info("result={}", resultA);

        //공통 로직1 종료

        //공통 로직2 시작
        log.info("start");
        String resultB = target.callB();    //호출하는 메서드만 다름 => 동적으로 처리 필요
        log.info("result={}", resultB);
        
        //공통 로직2 종료

    }

    @Test
    void reflection1() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");//hello.proxy.jdkdynamic.ReflectionTest에 있는 Hello 클래스
        //=> 클래스의 메타정보 획득

        Hello target = new Hello();

        //callA의 메서드 정보
        log.info("start");
        Method callA = classHello.getMethod("callA");
        Object resultA = callA.invoke(target); //target에 있는 메서드 호출
        log.info("resultA={}", resultA);

        //callB의 메서드 정보
        log.info("start");
        Method callB = classHello.getMethod("callB");
        Object resultB = callB.invoke(target); //target에 있는 메서드 호출
        log.info("resultB={}", resultB);
    }

    @Test
    void reflection2() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        /**
         * 동적으로 메서드가 호출되도록 변경
         */
        //클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");//hello.proxy.jdkdynamic.ReflectionTest에 있는 Hello 클래스
        //=> 클래스의 메타정보 획득

        Hello target = new Hello();

        //callA의 메서드 정보
        Method callA = classHello.getMethod("callA");
        dynamicCall(callA, target);

        //callB의 메서드 정보
        Method callB = classHello.getMethod("callB");
        dynamicCall(callB, target);
        /**
         * getMethod("callA") 안에 들어가는 문자를 실수로 getMethod("callZ") 로 작성해도 컴파일 오류가 발생하지 않는다.
         * 그러나 해당 코드를 직접 실행하는 시점에 발생하는 오류인 런타임 오류가 발생한다.
         * 따라서 리플렉션은 일반적으로 사용하면 안된다. 지금까지 프로그래밍 언어가 발달하면서 타입 정보를
         * 기반으로 컴파일 시점에 오류를 잡아준 덕분에 개발자가 편하게 살았는데, 리플렉션은 그것에 역행하는 방식이다.
         * 리플렉션은 프레임워크 개발이나 또는 매우 일반적인 공통 처리가 필요할 때 부분적으로 주의해서 사용해야 한다.
         */
    }

    private void dynamicCall(Method call, Object target) throws InvocationTargetException, IllegalAccessException {
        log.info("start");
        Object result = call.invoke(target);
        log.info("result={}", result);
    }

    @Slf4j
    static class Hello{
        public String callA() {
            log.info("callA");
            return "A";
        }
        public String callB() {
            log.info("callB");
            return "B";
        }

    }



}
