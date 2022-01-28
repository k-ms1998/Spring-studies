package hello.core.scan;

import hello.core.AutoAppConfig;
import hello.core.Order.OrderServiceImpl;
import hello.core.member.repository.MemberRepository;
import hello.core.member.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AutoAppConfigTest {

    @Test
    void basicScan() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class);
        MemberService memberService = ac.getBean(MemberService.class);
        Assertions.assertThat(memberService).isInstanceOf(MemberService.class);

//        OrderServiceImpl orderService = ac.getBean(OrderServiceImpl.class);

    }
}
