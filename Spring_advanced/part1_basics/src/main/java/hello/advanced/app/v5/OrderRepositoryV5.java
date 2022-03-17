package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceCallBack;
import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logTrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV5 {

    private final TraceTemplate template;

    public void save(String itemId) {
        //상품 저장
        template.execute("OrderRepositoryV5.request()", new TraceCallBack<String>() {
            @Override
            public String call() {
                if (itemId.equals("ex")) {
                    throw new IllegalStateException("예외 발생");
                }

                mySleep(1000);
                return "ok";
            } //람다로 구현 O
        });
    }

    private void mySleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
