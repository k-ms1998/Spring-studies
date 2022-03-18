package hello.proxy.pureproxy.decorator;

import hello.proxy.pureproxy.decorator.code.*;
import org.junit.jupiter.api.Test;

public class DecoratorPatternTest {

    @Test
    void noDecorator() {
        Component component = new RealComponent();
        DecoratorPatternClient client = new DecoratorPatternClient(component);

        client.execute();
    }

    /**
     * client -> messageDecorator -> realComponent 의 객체 의존 관계를 만들고 client.execute()를 호출
     * 실행 결과를 보면 MessageDecorator 가 RealComponent 를 호출하고 반환한 응답 메시지를 꾸며서 반환한 것을 확인할 수 있다
     */
    @Test
    void decoratorPattern1() {
        Component component = new RealComponent();
        Component decorator = new MessageDecorator(component);
        DecoratorPatternClient client = new DecoratorPatternClient(decorator);

        client.execute();
    }

    @Test
    void decoratorPattern2() {
        /**
         *  client -> timeDecorator 의존; timeDecorator -> messageDecorator 의존; messageDecorator -> RealComponent 의존
         */
        Component component = new RealComponent();
        Component messageDecorator = new MessageDecorator(component);
        Component timeDecorator = new TimeDecorator(messageDecorator);
        DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);

        client.execute();
        /**
         * 결과:
         * TimeDecorator 실행
         * MessageDecorator 실행
         * RealComponent 실행
         * MessageDecorator data -> *****data*****
         * TimeDecorator 종료 resultTime=13
         * result=*****data*****
         */
    }
}
