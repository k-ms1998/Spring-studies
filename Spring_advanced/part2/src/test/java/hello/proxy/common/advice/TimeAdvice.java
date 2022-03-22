package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class TimeAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeAdvice 실행");
        long startTime = System.currentTimeMillis();

        Object result = invocation.proceed(); //알아서 target을 찾아서 메서드 실행
        String methodName = invocation.getMethod().getName();
        log.info("methodName={}", methodName);  //proxy.save() => methodName = save, proxy.find() => methodName = find

        long endTime = System.currentTimeMillis();
        log.info("TimeAdvice 종료 resultTime={}", endTime - startTime);

        return null;
    }
}
