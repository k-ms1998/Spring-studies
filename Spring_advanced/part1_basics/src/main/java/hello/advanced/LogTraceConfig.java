package hello.advanced;

import hello.advanced.trace.logTrace.FieldLogTrace;
import hello.advanced.trace.logTrace.LogTrace;
import hello.advanced.trace.logTrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogTraceConfig {

    @Bean
    public LogTrace logTrace() {

//        return new FieldLogTrace();
        return new ThreadLocalLogTrace(); // app/v3에서 LogTrace를 FieldLogTrace에서 ThreadLocalLogTrace를 바꿔줌
        //=> 클라이언트 코드를 변경하지 않고 의존 관계 주입을 통해서 바꿔줌
    }

}
