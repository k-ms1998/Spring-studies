package hello.advanced.trace.template.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubClassLogic1 extends AbstractTemplate{
    //로직이 변하는 부분인 추상 메서드 call()을 오버라이딩해서 각 로직에 맞게 구현
    @Override
    protected void call() {
        //비즈니스 로직
        log.info("비즈니스 로직1 실행");
    }
}
