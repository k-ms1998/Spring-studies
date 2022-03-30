package hello.aop.example.aspect;

import hello.aop.example.ExampleService;
import hello.aop.example.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class RetryAspect {

    @Around("@annotation(retry)")
    public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        /**
         * 파라미터로 Retry 애노테이션을 받는다
         */

        log.info("[retry] {}, retry={}", joinPoint.getSignature(), retry);
        int maxRetry = retry.maxRetry();
        Exception exceptionHolder = null;
        for(int cnt = 1; cnt <= maxRetry; cnt++) { // maxRetry 횟수 만큼만 재시도를 한다; 무한으로 재시도시 서버 과부화가 걸릴 수 있음
            try {
                log.info("[retry] try count={}/{}", cnt, maxRetry);
                return joinPoint.proceed();
            } catch (Exception e) {
                exceptionHolder = e;
            }
        }

        throw exceptionHolder;
    }
}
