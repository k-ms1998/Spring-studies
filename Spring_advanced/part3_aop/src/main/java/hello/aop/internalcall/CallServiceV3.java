package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * 구조를 변경/분리 => 가장 추천되는 방법
 */
@Slf4j
@Component
public class CallServiceV3 {

    private final InternalService internalService;

    public CallServiceV3(InternalService internalService) {
        this.internalService = internalService;
    }

    public void external() {    //외부 메서드
        log.info("call external");

        internalService.internal(); //외부 메서드 호출
    }

    public void internal() { //내부 메서드

        log.info("call internal");
    }
}
