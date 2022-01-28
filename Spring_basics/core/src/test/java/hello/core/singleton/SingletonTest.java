package hello.core.singleton;

import hello.core.AppConfig;
import hello.core.member.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SingletonTest {
    
    @Test
    @DisplayName("스프링 없는 순수 DI 컨테이너")
    void pureContainer(){
        AppConfig appConfig = new AppConfig();

        //1.요청이 들어올때마다 새로운 객체 생성
        MemberService memberService1 = appConfig.memberService();

        //2.요청이 들어올때마다 새로운 객체 생성
        MemberService memberService2 = appConfig.memberService();

        //3.참조 값이 다른것을 확인
        System.out.println("memberService1=> "+memberService1);
        System.out.println("memberService2=> "+memberService2);

        Assertions.assertThat(memberService1).isNotSameAs(memberService2);
    }

    @Test
    @DisplayName("싱글톤 페턴을 적용한 객체 사용")
    void singletonServiceTest() {
        SingleService singleService1 = SingleService.getInstance();
        SingleService singleService2 = SingleService.getInstance();

        System.out.println("singleService1=> "+singleService1);
        System.out.println("singleService2=> "+singleService2);

        Assertions.assertThat(singleService1).isSameAs(singleService2);
        //두개의 singleService가 같은 객체를 가지고 있음
    }

    @Test
    @DisplayName("스프링 컨테이너와 싱글톤")
    void springContainer() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        //1.요청이 들어올때마다 새로운 객체 생성
        MemberService memberService1 = applicationContext.getBean("memberService", MemberService.class);

        //2.요청이 들어올때마다 새로운 객체 생성
        MemberService memberService2 = applicationContext.getBean("memberService", MemberService.class);

        //3.참조 값이 다른것을 확인
        System.out.println("memberService1=> "+memberService1);
        System.out.println("memberService2=> "+memberService2);

        Assertions.assertThat(memberService1).isSameAs( memberService2);
    }
}
