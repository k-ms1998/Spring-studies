package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * within 지시자는 특정 타입 내의 조인 포인트에 대한 매칭을 제한한다.
 * 쉽게 이야기해서 해당 타입이 매칭되면 그 안의 메서드(조인 포인트)들이 자동으로 매칭된다.
 * 문법은 단순한데 execution 에서 타입 부분만 사용한다고 보면 된다.
 */
public class WithinTest {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }


    @Test
    void withinExact() {
        pointcut.setExpression("within(hello.aop.member.MemberServiceImpl)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void withinStar() {
        pointcut.setExpression("within(hello.aop.member.*Service*)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void withinSubPackage() {
        pointcut.setExpression("within(hello.aop..*)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     *  표현식에 부모 타입을 지정하면 안된다는 점이다. 정확하게 타입이 맞아야 한다.
     *  이 부분에서 execution 과 차이가 난다.
     */
    @Test
    @DisplayName("타겟의 타입에만 직접 적용, 인터페이스를 선정하면 안된다.")
    void withinSuperTypeFalse() {
        pointcut.setExpression("within(hello.aop.member.MemberService)");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }
}
