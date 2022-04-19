package jpabook.jpashop.Service.Query;

import jpabook.jpashop.Domain.OrderItem;
import lombok.Data;

@Data
public class OrderItemQueryServiceCollectionDTO {
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryServiceCollectionDTO(OrderItem orderItem) {
        this.itemName = orderItem.getItem().getName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
    }
}
