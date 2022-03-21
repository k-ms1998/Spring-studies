package hello.proxy.cglib.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
public class TimeMethodInterceptor implements MethodInterceptor {

    private final Object target;

    public TimeMethodInterceptor(Object target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        /**
         * obj : CGLIB가 적용된 객체
         * method : 호출된 메서드
         * args : 메서드를 호출하면서 전달된 인수
         * proxy : 메서드 호출에 사용
         */
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = methodProxy.invoke(target, args); //'ConcreteService 호출' 출력

        long endTime = System.currentTimeMillis();
        log.info("TimeProxy 종료 resultTime={}", endTime - startTime);

        return null;
    }
}
