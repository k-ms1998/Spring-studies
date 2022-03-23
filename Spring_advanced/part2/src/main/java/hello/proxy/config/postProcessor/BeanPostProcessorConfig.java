package hello.proxy.config.postProcessor;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.postProcessor.postprocessor.PackageLogTracePostProcessor;
import hello.proxy.config.proxyFactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * app/v3 에도 BeanPostProcessor를 통해 proxy 등록
 */
@Slf4j
@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class BeanPostProcessorConfig {

    @Bean
    public PackageLogTracePostProcessor packageLogTracePostProcessor(LogTrace logTrace) {
       
        return new PackageLogTracePostProcessor("hello.proxy.app", getAdvisor(logTrace));
    }

    private Advisor getAdvisor(LogTrace logTrace) {
        //Pointcut => 필터링되는 조건
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*"); //no-log일때는 로그가 출력이 되지 않도록 필터링하지 않을 메서드 이름들 설정해줌

        //Advice 생성 => 실질적인 부가기능을 담은 구현체
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);

        //Advisor 생성 => advisor = pointcut + advice
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);

        return advisor;
    }
}
