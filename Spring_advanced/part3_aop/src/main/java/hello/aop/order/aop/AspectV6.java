package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;

@Slf4j
public class AspectV6 {

    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable
    {
        try {
            //@Before
            log.info("[around][트랜잭션 시작] {}", joinPoint.getSignature());

            Object result = joinPoint.proceed(); //다음 어드바이스나 타겟 실행
            
            //@AfterReturning
            log.info("[around][트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            //@AfterThrowing
            log.info("[around][트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            //@After
            log.info("[around][리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    /**
     * 조인 포인트 실행 전
     */
    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore(JoinPoint joinPoint) {
        log.info("[before] {}", joinPoint.getSignature());
    }

    /**
     * 메서드 실행이 정상적으로 반환될 때 실행
     * */
    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    public void doReturn(JoinPoint joinPoint, Object result) {
        //returning 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다

        log.info("[return] {} return={}", joinPoint.getSignature(), result);
    }

    /**
     * 메서드 실행이 예외를 던져서 종료될 때 실행
     * */
    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) {
        //throwing 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다

        log.info("[ex] {} message={}", joinPoint.getSignature(), ex.getMessage());
    }

    /**
     * 메서드 실행이 종료되면 실행된다.(finally를 생각하면 된다.)
     * 정상 및 예외 반환 조건을 모두 처리한다.
     * 일반적으로 리소스를 해제하는 데 사용한다.
     * */
    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    }

}

