package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

@Slf4j
public class ExecutionTest {

    AspectJExpressionPointcut pointcut= new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    void printMethod() {
        log.info("helloMethod={}", helloMethod);
    }

    @Test
    void exactMatch() {
        /**
         * 접근제어자?: public
         * 반환타입: String
         * 선언타입?: hello.aop.member.MemberServiceImpl
         * 메서드이름: hello
         * 파라미터: (String)
         * 예외?: 생략
         */
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void allMatch() {
        /**
         * 접근제어자?: 생략
         * 반환타입: *
         * 선언타입?: 생략
         * 메서드이름: *
         * 파라미터: (..)
         * 예외?: 없음
         * cf: 파라미터에서 .. 은 파라미터의 타입과 파라미터 수가 상관없다는 뜻
         */
        pointcut.setExpression("execution(* *(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatch() {
        pointcut.setExpression("execution(* hello(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar1() {
        pointcut.setExpression("execution(* hel*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar2() {
        pointcut.setExpression("execution(* *el*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchFalse() {
        pointcut.setExpression("execution(* noMatch(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageExactMatch1() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactMatch2() {
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactFalse() {
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * 패키지에서 . , .. 의 차이를 이해해야 한다.
     * . : 정확하게 해당 위치의 패키지
     * .. : 해당 위치의 패키지와 그 하위 패키지도 포함
     */
    @Test
    void packageMatchSubPackage1() {
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageMatchSubPackage2() {
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchSuperType() {
        /**
         * 부모 타임 match 허용
         * execution 에서는 MemberService 처럼 부모 타입을 선언해도 그 자식 타입은 매칭된다.
         * 다형성에서 부모타입 = 자식타입 이 할당 가능하다는 점을 떠올려보면 된다
         */
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchInternalTrue() throws NoSuchMethodException {
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);

        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Assertions.assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchInternalFalse() throws NoSuchMethodException {
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);

        /**
         * internal() 은 부모타입에는 없고, 자식 타입에만 있는 메서드임으로 매칭 X
         */
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Assertions.assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * String 타입의 파라미터 허용
     * (String)
     */
    @Test
    void argsMatch() {
        pointcut.setExpression("execution(* *(String))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 파라미터가 없어야 함
     * ()
     */
    @Test
    void noArgs() {
        pointcut.setExpression("execution(* *())");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * 정확히 하나의 파라미터 허용, but 모든 타입 허용
     * (Xx)
     */
    @Test
    void argsMatchStar() {
        pointcut.setExpression("execution(* *(*))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 숫자와 무관하게 모든 파라미터 허용, 모든 타입 허용
     * (), (Xxx), (Xxx, Xxx)
     */
    @Test
    void argsMatchAll() {
        pointcut.setExpression("execution(* *(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * String 타입으로 시작
     * (String), (String, Xxx), (String, Xxx, Xxx)
     */
    @Test
    void argsMatchComplex() {
        pointcut.setExpression("execution(* *(String, ..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
}
