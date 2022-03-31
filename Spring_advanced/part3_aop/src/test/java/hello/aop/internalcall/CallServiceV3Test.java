package hello.aop.internalcall;

import hello.aop.internalcall.aspect.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(CallLogAspect.class)
class CallServiceV3Test {

    @Autowired
    CallServiceV3 callService;

    @Test
    void external() {
        /**
         * 결과:
         * aop=void hello.aop.internalcall.CallServiceV3.external()
         * hello.aop.internalcall.CallServiceV3     : call external
         * aop=void hello.aop.internalcall.InternalService.internal() => InternalService 의 internal() 이 호출 됨
         * hello.aop.internalcall.InternalService   : call internal
         */
        callService.external();
    }

    @Test
    void internal() {
        callService.internal();
    }
}