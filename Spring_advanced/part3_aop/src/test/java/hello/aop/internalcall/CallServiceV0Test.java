package hello.aop.internalcall;

import hello.aop.internalcall.aspect.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Import(CallLogAspect.class)
class CallServiceV0Test {

    @Autowired
    CallServiceV0 callService; //Proxy

    @Test
    void external() {
        callService.external();
        /**
         *  callServiceV0.external() 안에서 internal() 을 호출할 때 발생한다. 그러므로, 프록시 생성 X
         *  이때는 CallLogAspect 어드바이스가 호출되지 않는다
         *
         *  결과:
         *  aop=void hello.aop.internalcall.CallServiceV0.external()
         *  hello.aop.internalcall.CallServiceV0     : call external
         *  hello.aop.internalcall.CallServiceV0     : call internal => AOP 적용 X; AOP 적용이 됐으면  aop=void hello.aop.internalcall.CallServiceV0.internal()이 직전에 출력 됐어야 함
         */
    }

    @Test
    void internal() {
        callService.internal();

        /**
         * 외부에서 호출하는 경우 프록시를 거치기 때문에 internal() 도 CallLogAspect 어드바이스가 적용된 것을 확인할 수 있다
         *
         * 결과:
         * aop=void hello.aop.internalcall.CallServiceV0.internal()
         * hello.aop.internalcall.CallServiceV0     : call internal
         */
    }
}