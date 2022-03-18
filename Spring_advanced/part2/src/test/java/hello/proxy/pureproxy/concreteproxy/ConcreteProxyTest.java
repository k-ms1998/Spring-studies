package hello.proxy.pureproxy.concreteproxy;

import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy;
import org.junit.jupiter.api.Test;

public class ConcreteProxyTest {

    @Test
    void noProxy() {
        ConcreteLogic logic = new ConcreteLogic();
        ConcreteClient client = new ConcreteClient(logic);

        client.execute();
    }

    /**
     * client -> timeProxy -> concreteLogic
     */
    @Test
    void addProxy() {
        ConcreteLogic logic = new ConcreteLogic();
        TimeProxy timeProxy = new TimeProxy(logic); //TimeProxy는 ConcreteLogic의 자식 클래스이므로, 다형성에 의해 ConcreteClient에 TimeProxy가 들어가도 된다
        ConcreteClient client = new ConcreteClient(timeProxy);

        client.execute();
    }


}
