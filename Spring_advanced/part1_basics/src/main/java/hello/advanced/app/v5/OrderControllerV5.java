package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceCallBack;
import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logTrace.LogTrace;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // == @Controller + @ResponseBody
@RequestMapping("/v5")
@RequiredArgsConstructor
public class OrderControllerV5 {

    private final OrderServiceV5 orderService;
    private final TraceTemplate template;

//    @Autowired
//    public OrderControllerV5(OrderServiceV5 orderService, LogTrace trace) {
//        this.orderService = orderService;
//        this.template = new TraceTemplate(trace);
//    } //TraceTemplate을 빈 등록해서 사용하면 @RequiredArgsConstructor로 대체 가능

    @GetMapping("/request")
    public String request(String itemId) {
        return template.execute("OrderController.request()",
                () -> {
                    orderService.orderItem(itemId);
                    return "ok";
                });
    }
}
