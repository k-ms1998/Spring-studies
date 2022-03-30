package hello.aop.pointcut;

import hello.aop.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

/**
 * application.properties :
 * spring.aop.proxy-target-class=true => Default; CGLIB 프록시 생성
 * spring.aop.proxy-target-class=false => JDK 동적 프록시 생성
 *
 */
@Slf4j
@SpringBootTest(properties = "spring.aop.proxy-target-class=false")
@Import(ThisTargetTest.ThisTargetAspect.class)
public class ThisTargetTest {

    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");

    }

    @Slf4j
    @Aspect
    static class ThisTargetAspect {

        //부모 타입 허용
        @Around("this(hello.aop.member.MemberService)")
        public Object doThisInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[this-interface]{}", joinPoint.getSignature());

            return joinPoint.proceed();
        }

        //부모 타입 허용
        @Around("target(hello.aop.member.MemberService)")
        public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[target-interface]{}", joinPoint.getSignature());

            return joinPoint.proceed();
        }

        //부모 타입 허용
        @Around("this(hello.aop.member.MemberServiceImpl)")
        public Object doThisConcrete(ProceedingJoinPoint joinPoint) throws Throwable {
            /**
             * CGLIB: O
             * JDK 동적 프록시: X
             * JDK 동적 프록시를 사용하면 this(hello.aop.member.MemberServiceImpl) 로 지정한 [this-concrete] 부분이 출력되지 않는 것을 확인할 수 있다
             */
            log.info("[this-concrete]{}", joinPoint.getSignature());

            return joinPoint.proceed();
        }

        //부모 타입 허용
        @Around("target(hello.aop.member.MemberServiceImpl)")
        public Object doTargetConcrete(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[target-concrete]{}", joinPoint.getSignature());

            return joinPoint.proceed();
        }
        
        
        
    }


}
