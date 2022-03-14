package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.helloTrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {

    private final OrderRepositoryV2 orderRepository;
    private final HelloTraceV2 trace;

    public void orderItem(TraceId traceId, String itemId) {
        //상품 주문
        TraceStatus status = null;
        try {
            status = trace.beginSync(traceId, "OrderService.orderItem()"); //beginSync에서 id는 유지하지만, level은 1 증가 된 TraceId 생성
            orderRepository.save(status.getTraceId(), itemId); //OrderRepository에 level을 전달하기 위해 현재의 traceId를 넘겨준다
            trace.end(status);

        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }

    }
}
