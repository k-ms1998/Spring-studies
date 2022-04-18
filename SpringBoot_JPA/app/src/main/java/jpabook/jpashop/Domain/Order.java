package jpabook.jpashop.Domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id; //PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //FK => 그러므로, Member와 Order의 연관 관계에서 Order를 주인으로 설정

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태: [ORDER, CANCEL]

    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    /**
     * ==생성 메서드==
     * 생성자 대신 생성 메서드를 사용하는 이유는 정적 팩토리 메서드 패턴을 만족시키기 위함입니다.
     */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        //OrderItem 생성 -> createOrderItem() 호출 -> Order 생성 -> createOrder() 호출
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    /**
     * ==비즈니스 로직==
     */
    // =주문 취소=
    public void cancelOrder() {
        //이미 배달 완료이면 취소가 불가능
        if (this.getDelivery().getStatus() == DeliveryStatus.COMPLETE) {
            throw new IllegalStateException("이미 배송이 완료된 상품은 취소가 불가능");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : this.getOrderItems()) {
            //주문한 상품의 주문을 취소하면, 취소한 만큼 재고 수량을 변화 시켜줘야됨
            orderItem.cancel();
        }
    }

    /**
     * ==조회 로직==
     */
    // =전체 주문 가격 조회=
    public int getTotalPrice() {
//        int totalPrice = 0;
//        for (OrderItem orderItem : this.getOrderItems()) {
//            totalPrice += orderItem.calculateTotalPrice();
//        }
//        return totalPrice;

        return this.getOrderItems().stream()
                .mapToInt((o) -> {
                    return o.calculateTotalPrice();
                }).sum();

    }
    /**
     * 직접 필드를 접근하지 않고 Getter를 사용하는 이유는 OrderItem 하단 부분 주석 확인
     */

}

