package hello.aop.member.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메서드에 붙일수 있는 annotation
@Retention(RetentionPolicy.RUNTIME) // 애플리케이션이 실행될때까지 유지
public @interface MethodAop {
    String value();
    String name();
}
