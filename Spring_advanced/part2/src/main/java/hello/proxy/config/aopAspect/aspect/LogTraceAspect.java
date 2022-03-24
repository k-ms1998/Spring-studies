package hello.proxy.config.aopAspect.aspect;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 자동 프록시 생성기는 @Aspect 를 보고 어드바이저( Advisor )로 변환해고, 어드바이저를 기반으로 프록시를 생성한다
 *
 * @Aspect를 어드바이저로 변환해서 저장하는 과정을 알아보자
 * 1. 실행: 스프링 애플리케이션 로딩 시점에 자동 프록시 생성기를 호출한다.
 * 2. 모든 @Aspect 빈 조회: 자동 프록시 생성기는 스프링 컨테이너에서 @Aspect 애노테이션이 붙은 스프링 빈을 모두 조회한다.
 * 3. 어드바이저 생성: @Aspect 어드바이저 빌더를 통해 @Aspect 애노테이션 정보를 기반으로 어드바이저를 생성한다.
 * 4. @Aspect 기반 어드바이저 저장: 생성한 어드바이저를 @Aspect 어드바이저 빌더 내부에 저장한다.
 *
 * 실무에서 프록시를 적용할 때는 대부분이 이 방식을 사용한다
 */
@Slf4j
@Aspect //  애노테이션 기반 프록시를 적용할 때 필요하다.
public class LogTraceAspect {

    private final LogTrace logTrace;

    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    /**
     * @Around("execution(* hello.proxy.app..*(..))") :
     * @Around 의 값에 포인트컷 표현식을 넣는다. 표현식은 AspectJ 표현식을 사용한다.
     * @Around 의 메서드는 어드바이스( Advice )가 된다
     *
     * ProceedingJoinPoint joinPoint :
     * 어드바이스에서 살펴본 MethodInvocation invocation 과 유사한 기능이다.
     * 내부에 실제 호출 대상, 전달 인자, 그리고 어떤 객체와 어떤 메서드가 호출되었는지 정보가 포함되어 있다
     */
    @Around("execution(* hello.proxy.app..*(..))")  // == Pointcut
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable { // ==Advice
        TraceStatus status = null;
        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin("Aspect: "+message);

            //로직 호출
            Object result = joinPoint.proceed(); //실제 호출 대상( target )을 호출한다.

            logTrace.end(status);
            return result;

        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
