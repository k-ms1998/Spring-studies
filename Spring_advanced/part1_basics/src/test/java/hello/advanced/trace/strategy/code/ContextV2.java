package hello.advanced.trace.strategy.code;

import lombok.extern.slf4j.Slf4j;

/**
 * 전략을 파라미터로 전달 받는 방식
 * 변하지 않는 로직
 */
@Slf4j
public class ContextV2 {

    public void execute(Strategy strategy) {
        Long startTime = System.currentTimeMillis();
        strategy.call();    //위임
        Long endTime = System.currentTimeMillis();

        log.info("resultTime={}", endTime - startTime);
    }
}
