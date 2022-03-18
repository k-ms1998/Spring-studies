package hello.proxy.pureproxy.proxy;

import hello.proxy.pureproxy.proxy.code.CacheProxy;
import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import org.junit.jupiter.api.Test;

public class ProxyPatternTest {

    @Test
    void noProxyTest() {
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);

        /**
         * RealSubject에서 호출 시 1초씩 걸리므로, 총 3초 소요
         */
        for (int i = 0; i < 3; i++) {
            client.execute();
        }
    }

    @Test
    void cacheProxyTest() {
        RealSubject realSubject = new RealSubject();
        CacheProxy cacheProxy = new CacheProxy(realSubject);
        ProxyPatternClient client = new ProxyPatternClient(cacheProxy);

        /**
         * CacheProxy에서 호출 후, 처음에는 저장된 캐시 값이 없기 때문에 RealSubject호출
         * 2번째 부터는, 캐시에 저장된 값이 있기 때문에 RealSubject 호출 없이 바로 CacheProxy에서 호출
         * 총 소요 시간 < 1초
         *
         * 1. client의 cacheProxy 호출 cacheProxy에 캐시 값이 없다. realSubject를 호출, 결과를 캐시에 저장 (1초)
         * 2. client의 cacheProxy 호출 cacheProxy에 캐시 값이 있다. cacheProxy에서 즉시 반환 (0초)
         * 3. client의 cacheProxy 호출 cacheProxy에 캐시 값이 있다. cacheProxy에서 즉시 반환 (0초)
         */
        for (int i = 0; i < 3; i++) {
            client.execute();
        }

    }

}
