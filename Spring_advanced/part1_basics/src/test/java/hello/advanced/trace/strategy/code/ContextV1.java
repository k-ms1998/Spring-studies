package hello.advanced.trace.strategy.code;

import lombok.extern.slf4j.Slf4j;

/**
 * 필드에 전력을 보관하는 방식
 * 변하지 않는 로직
 */
@Slf4j
public class ContextV1 {

    private Strategy strategy;

    public ContextV1(Strategy strategy) {
        this.strategy = strategy;
    }

    public void execute() {
        Long startTime = System.currentTimeMillis();
        strategy.call();    //위임
        Long endTime = System.currentTimeMillis();

        log.info("resultTime={}", endTime - startTime);
    }
}
