package hello.proxy.config.autoProxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.postProcessor.postprocessor.PackageLogTracePostProcessor;
import hello.proxy.config.proxyFactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 자동 프록시 생성기의 작동 과정을 알아보자
 * 1. 생성: 스프링이 스프링 빈 대상이 되는 객체를 생성한다. ( @Bean , 컴포넌트 스캔 모두 포함)
 * 2. 전달: 생성된 객체를 빈 저장소에 등록하기 직전에 빈 후처리기에 전달한다.
 * 3. 모든 Advisor 빈 조회: 자동 프록시 생성기 - 빈 후처리기는 스프링 컨테이너에서 모든 Advisor 를 조회한다.
 * ****4. 프록시 적용 대상 체크: 앞서 조회한 Advisor 에 포함되어 있는 포인트컷을 사용해서 해당 객체가 프록시를 적용할 대상인지 아닌지 판단한다.****
 * 이때 객체의 클래스 정보는 물론이고, 해당 객체의 모든 메서드를 포인트컷에 하나하나 모두 매칭해본다. 그래서 조건이 하나라도 만족하면 프록시 적용 대상이 된다.
 * 예를 들어서 10개의 메서드 중에 하나만 포인트컷 조건에 만족해도 프록시 적용 대상이 된다.
 * 5. 프록시 생성: 프록시 적용 대상이면 프록시를 생성하고 반환해서 프록시를 스프링 빈으로 등록한다. 만약 프록시 적용 대상이 아니라면 원본 객체를 반환해서 원본 객체를 스프링 빈으로 등록한다.
 * 6. 빈 등록: 반환된 객체는 스프링 빈으로 등록된다
 * 
 * cf: 스프링은 Advisor 가 여러개 있어도, 프록시는 무조건 하나만 생성
 */
@Slf4j
@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AutoProxyConfig {
    /**
     * 자동 프록시 생성기는 포인트컷을 사용해서 해당 빈이 프록시를 생성할 필요가 있는지 없는지 체크한다.
     * 클래스 + 메서드 조건을 모두 비교한다. 이때 모든 메서드를 체크하는데, 포인트컷 조건에 하나하나 매칭해본다.
     * 만약 조건에 맞는 것이 하나라도 있으면 프록시를 생성한다.
     * 예) orderControllerV1 은 request() , noLog() 가 있다. 여기에서 request() 가 조건에 만족하므로 프록시를 생성한다
     */

//    @Bean
    public Advisor advisor1(LogTrace logTrace) {
        //Pointcut => 필터링되는 조건
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*"); //Name Match가 되는 메서드들에 대해서 프록시를 모두 적용한다
        /**
         * 문제점:
         * 스프링이 내부에서 사용하는 빈에도 메서드 이름에 request 라는 단어만 들어가 있으면 프록시가 만들어지고 되고, 어드바이스도 적용되는 것이다
         * => advisor2 에서 해결
         */

        //Advice 생성 => 실질적인 부가기능을 담은 구현체
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);

        //Advisor 생성 => advisor = pointcut + advice
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);

        return advisor;
    }

    /**
     * AspectJ라는 AOP에 특화된 포인트컷 표현식을 적용할 수 있다
     */
    @Bean
    public Advisor advisor2(LogTrace logTrace) {
        //Pointcut => 필터링되는 조건
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        /**
         * execution(* hello.proxy.app..*(..)) : AspectJ가 제공하는 포인트컷 표현식이다
         * * : 모든 반환 타입
         * hello.proxy.app.. : 해당 패키지와 그 하위 패키지
         * *(..) : * 모든 메서드 이름, (..) 파라미터는 상관 없음
         * => hello.proxy.app 패키지와 그 하위 패키지의 모든 메서드는 포인트컷의 매칭 대상이 된다
         */
        pointcut.setExpression
                ("execution(* hello.proxy.app..*(..))" +
                        "&& !execution(* hello.proxy.app..noLog(..))"); //해당 위치에 있어야 프록시 적용 대상 && 프록시 적용에서 제외할 대상
        //hello.proxy.app 패키지와 하위 패키지의 모든 메서드는 포인트컷의 매칭하되, noLog() 메서드는 제외하라는 뜻이다

        //Advice 생성 => 실질적인 부가기능을 담은 구현체
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);

        //Advisor 생성 => advisor = pointcut + advice
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);

        return advisor;
    }
}
