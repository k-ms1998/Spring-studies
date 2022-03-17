package hello.proxy.app.v3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * App V3
 * => 컴포넌트 스캔으로 스프링 빈 자동 등록
 * 각각 @RestController , @Service , @Repository 애노테이션을 가지고 있기 때문에 컴포넌트 스캔의 대상이 된다
 * config 생성 후 수동으로 config 클래스 빈 등록 필요 X
 */
@Slf4j
@RestController
public class OrderControllerV3 {

    private final OrderServiceV3 orderService;

    public OrderControllerV3(OrderServiceV3 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v3/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }

    @GetMapping("/v3/no-log")
    public String noLog(String itemId) {

        return "ok";
    }
}