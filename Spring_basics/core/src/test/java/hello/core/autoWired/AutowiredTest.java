package hello.core.autoWired;

import hello.core.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class AutowiredTest {
    @Test
    void AutowiredOption() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
    }

    static class TestBean{
        /*
        @Autowired //Default: required=true
        public void setNoBeanDefault(Member noBean) {
            System.out.println("noBean0:"+noBean);
        }*/ //UnsatisfiedDependencyException; Member는 현재 스프링 빈이 아니기 때문에 오류가 뜸

        @Autowired(required = false)
        public void setNoBean1(Member noBean) {
           System.out.println("noBean1:"+noBean); //호출X
        }

        @Autowired
        public void setNoBean2(@Nullable Member noBean) {
            System.out.println("noBean2:"+noBean); //Output: "noBean2:null"
        }


        @Autowired
        public void setNoBean3(Optional<Member> noBean) {
            System.out.println("noBean3:"+noBean); // Output: "noBean3:Optional.empty"
        }
    }
}
