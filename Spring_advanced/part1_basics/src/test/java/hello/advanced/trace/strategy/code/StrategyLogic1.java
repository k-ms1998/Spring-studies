package hello.advanced.trace.strategy.code;

import lombok.extern.slf4j.Slf4j;

/**
 * 각 필요에 맞게 로직을 구현
 */
@Slf4j
public class StrategyLogic1 implements Strategy{
    @Override
    public void call() {
        log.info("비즈니스 로직1 실행");  
    }
}
