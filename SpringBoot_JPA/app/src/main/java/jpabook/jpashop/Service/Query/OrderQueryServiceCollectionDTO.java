package jpabook.jpashop.Service.Query;

import jpabook.jpashop.Api.OrderCollectionApiController;
import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderQueryServiceCollectionDTO {
    private Long orderId;
    private String username; //주문자 이름
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address; //배송지
    private List<OrderItemQueryServiceCollectionDTO> orderItems;

    /**
     * Response를 반환 할때 엔티티 반환 X
     * 그 뜻은, DTO에서도 엔티티 반환 X
     * 그러므로, OrderItem도 DTO로 변환해서 반환
     * 또한, OrderItem도 DTO로 변환하면 클라이언트가 원하는 값만 보낼 수 있음
     */

    public OrderQueryServiceCollectionDTO(Order order) {
        this.orderId = order.getId();
        this.username = order.getMember().getUsername();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.address = order.getDelivery().getAddress();
        this.orderItems = order.getOrderItems().stream()
                .map((o) -> new OrderItemQueryServiceCollectionDTO(o))
                .collect(Collectors.toList());
    }
}
