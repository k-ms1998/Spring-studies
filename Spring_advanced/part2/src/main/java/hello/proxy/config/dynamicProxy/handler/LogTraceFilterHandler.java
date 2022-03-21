package hello.proxy.config.dynamicProxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogTraceFilterHandler implements InvocationHandler {

    private final Object target;
    private final LogTrace logTrace;
    private final String[] patterns; //메서드 이름이 pattern에 있으면 로그를 남기도록 함

    public LogTraceFilterHandler(Object target, LogTrace logTrace, String[] patterns) {
        this.target = target;
        this.logTrace = logTrace;
        this.patterns = patterns;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //메서드 이름 필터
        String methodName = method.getName();

        if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
            //메서드 이름이 patterns에 매치되지 않으면 로그를 출력하지 않는다
            return method.invoke(target, args);
        }

        TraceStatus status = null;
        try {
            /**
             * 직접 개발할 때는 "OrderController.request()" 와 같이 프록시마다 호출되는 클래스와 메서드 이름을 직접 남겼다.
             * 이제는 Method 를 통해서 호출되는 메서드 정보와 클래스 정보를 동적으로 확인할 수 있기 때문에 이 정보를 사용하면 된다.
             */
            String message = method.getDeclaringClass().getSimpleName() + method.getName() + "()";
            status = logTrace.begin("JDKDynamicProxy: "+message);

            //로직 호출
            Object result = method.invoke(target, args);

            logTrace.end(status);
            return result;

        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
