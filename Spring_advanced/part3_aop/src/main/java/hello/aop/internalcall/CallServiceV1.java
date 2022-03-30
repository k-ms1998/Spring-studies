package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 내부 호출을 해결하는 가장 간단한 방법은 자기 자신을 의존관계 주입 받는 것이다
 */
@Slf4j
@Component
public class CallServiceV1 {

    /**
     * 스프링에서 AOP가 적용된 대상을 의존관계 주입 받으면 주입 받은 대상은 실제 자신이 아니라 프록시 객체이다.
     * 주입 받은 callServiceV1 은 프록시이다. 따라서 프록시를 통해서 AOP를 적용할 수 있다
     */
    private CallServiceV1 callServiceV1;

    @Autowired
    public void setCallServiceV1(CallServiceV1 callServiceV1) {
        /**
         * 생성자 주입시 오류가 발생한다. 본인을 생성하면서 주입해야 하기 때문에 순환 사이클이 만들어진다.
         * 반면에 수정자 주입은 스프링이 생성된 이후에 주입할 수 있기 때문에 오류가 발생하지 않는다
         */
        this.callServiceV1 = callServiceV1;
    }

    public void external() {    //외부 메서드
        log.info("call external");
        callServiceV1.internal(); //this가 아닌 프록시 객체의 internal() 호출; 내부 메서드 호출에서 외부 메서드 호출이 됨
    }

    public void internal() { //내부 메서드
        log.info("call internal");
    }
}
