package jpabook.jpashop.Repository.DTO;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderQueryDTO {
    private Long orderId;
    private String username; //주문자 이름
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address; //배송지

    public OrderQueryDTO(Long orderId, String username, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.username = username;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}