package jpabook.jpashop.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
public class Order extends BaseEntity{

    @Id
    @GeneratedValue() //Default strategy = auto
    @Column(name = "ORDER_ID")
    private Long id;

    /**
     * @ManyToOne && @OneToOne DEFAULT는 FetchType.EAGER
     * 그러므로, FetchType.LAZY로 설정해주는게 권장됨
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELIVERY_ID")
    private Delivery delivery;

    /**
     * 실무에서는 LAZY Loading/지연 로딩을 사용하는게 적합
     * Because, 즉시 로딩시 예상하지 못한 SQL문 발생 && JPQL에서 N+1 문제를 일으킨다
     * 즉시 로딩시, 자동으로 연관된 테이블을 조인(JOIN)해서 가죠오기 때문에 엄청난 성능 저하 발생 가능성이 있음
     */
    @ManyToOne(fetch = FetchType.LAZY) // DEFAULT == FetchType.EAGER (==즉시 로딩)
//    @ManyToOne // N:1 관계; Member가 여러개의 주문을 할 수 있지만, 주문서는 하나의 Member랑만 연관 되어 있음
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
