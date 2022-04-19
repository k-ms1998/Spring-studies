package jpabook.jpashop.Repository.Order.Query;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class OrderItemQueryCollectionDTO {

    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryCollectionDTO(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
