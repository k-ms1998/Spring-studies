package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallServiceV0 {

    public void external() {    //외부 메서드
        log.info("call external");
        this.internal(); //내부 메서드 호출
    }

    public void internal() { //내부 메서드
        log.info("call internal");
    }
}
