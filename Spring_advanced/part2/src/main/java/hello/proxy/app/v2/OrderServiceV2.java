package hello.proxy.app.v2;

import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;

public class OrderServiceV2{

    private final OrderRepositoryV2 orderRepository;

    public OrderServiceV2(OrderRepositoryV2 orderRepository) {

        this.orderRepository = orderRepository;
    }

    public void orderItem(String itemId) {

        orderRepository.save(itemId);
    }
}
