package jpabook.jpashop.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue() //Default strategy = auto
    @Column(name = "ORDER_ID")
    private Long id;


    @ManyToOne // N:1 관계; Member가 여러개의 주문을 할 수 있지만, 주문서는 하나의 Member랑만 연관 되어 있음
    @JoinColumn(name = "MEMBER_ID") // ... member m JOIN orders o ON m.MEMBER_ID = o.MEMBER_ID;
    private Member memberId; // memberId에 Member 객체 자체를 주입 함

    private LocalDateTime orderDate;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "orderId")
    private List<OrderItem> orderItemList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMemberId() {
        return memberId;
    }

    public void setMemberId(Member memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
