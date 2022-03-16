package hello.advanced.trace.template;

import hello.advanced.trace.template.code.AbstractTemplate;
import hello.advanced.trace.template.code.SubClassLogic1;
import hello.advanced.trace.template.code.SubClassLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TemplateMethodTest {

    @Test
    void templateMethodV0() {
        logic1();
        logic2();
        //변하는 로직: 비즈니스 로직
        //변하지 않는 로직: 시간 측정 로직
        //=> 변하지 않는 로직을 모듈화해서 코드를 간소화하는 것이 목표 => V1
    }

    /**
     * 템플릿 메서드 패턴 적용
     */
    @Test
    void templateMethodV1() {
        AbstractTemplate template1 = new SubClassLogic1();
        template1.execute();

        AbstractTemplate template2 = new SubClassLogic2();
        template2.execute();
    }

    private void logic1() {
        Long startTime = System.currentTimeMillis();

        //비즈니스 로직 실행
        log.info("비즈니스 로직1 실행");

        //비즈니스 로직 종료
        Long endTime = System.currentTimeMillis();

        log.info("resultTime1={}", endTime - startTime);
    }

    private void logic2() {
        Long startTime = System.currentTimeMillis();

        //비즈니스 로직 실행
        log.info("비즈니스 로직2 실행");

        //비즈니스 로직 종료
        Long endTime = System.currentTimeMillis();

        log.info("resultTime2={}", endTime - startTime);
    }
}
