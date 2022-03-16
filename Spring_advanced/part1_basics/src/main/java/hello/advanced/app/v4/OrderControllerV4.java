package hello.advanced.app.v4;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logTrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // == @Controller + @ResponseBody
@RequestMapping("/v4")
@RequiredArgsConstructor
public class OrderControllerV4 {

    private final OrderServiceV4 orderService;
    private final LogTrace trace;

    @GetMapping("/request")
    public String request(@RequestParam String itemId) {
        AbstractTemplate<String> template = new AbstractTemplate<String>(trace){
            @Override
            protected String call() {
                orderService.orderItem(itemId);
                return "ok";
            }
        };

        String result = template.execute("OrderControllerV4.request()");

        return result;
    }
}
