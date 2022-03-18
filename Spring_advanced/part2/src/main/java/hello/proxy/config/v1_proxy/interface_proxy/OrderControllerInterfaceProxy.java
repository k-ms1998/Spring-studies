package hello.proxy.config.v1_proxy.interface_proxy;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderControllerInterfaceProxy implements OrderControllerV1 {

    private final OrderControllerV1 target;
    private final LogTrace logTrace;

    @Override
    public String request(String itemId) {
        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderControllerV1.request()");

            //target 호출
            target.request(itemId);

            logTrace.end(status);

            return "ok";

        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }


    @Override
    public String noLog() {
        return target.noLog();
    }
}
