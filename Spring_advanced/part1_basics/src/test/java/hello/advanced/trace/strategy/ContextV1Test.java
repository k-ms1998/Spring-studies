package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.ContextV1;
import hello.advanced.trace.strategy.code.Strategy;
import hello.advanced.trace.strategy.code.StrategyLogic1;
import hello.advanced.trace.strategy.code.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextV1Test {

    /**
     * 전략 패턴 사용
     */
    @Test
    void strategyV1() {
        StrategyLogic1 logic1 = new StrategyLogic1();
        ContextV1 context1 = new ContextV1(logic1);

        StrategyLogic2 logic2 = new StrategyLogic2();
        ContextV1 context2 = new ContextV1(logic2);


        context1.execute();
        context2.execute();
    }

    @Test
    void strategyV2() {
        ContextV1 context1 = new ContextV1(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직1 실행");
            }
        });
        ContextV1 context2 = new ContextV1(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직2 실행");
            }
        });

        context1.execute();
        context2.execute();
    }

    @Test
    void strategyV3() {
        //인터페이스 Strategy에 메서드가 하나만 있으므로, 람다로 구현 가능
        ContextV1 context1 = new ContextV1(() -> {
            log.info("비즈니스 로직1 실행");
        });
        ContextV1 context2 = new ContextV1(() -> {
            log.info("비즈니스 로직2 실행");
        });

        context1.execute();
        context2.execute();
        

    }
}
