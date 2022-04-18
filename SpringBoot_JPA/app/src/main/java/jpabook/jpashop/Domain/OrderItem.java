package jpabook.jpashop.Domain;

import jpabook.jpashop.Domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 가격
    private int count; //주문 수량


    /**
     * 생성 메서드
     * 해당 메서드를 통해서 필요한 엔티티들을 set 해줌
     */
    static public OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice); //상품에 대한 할인 등이 적용 될 수도 있기 때문에 item.getPrice() 대신 orderPrice를 인자로 받아서 set
        orderItem.setCount(count);

        item.decreaseStock(count);

        return orderItem;
    }

    /**
     * 비즈니스 로직
     */
    public void cancel() {
        //주문 수량만큼 상품의 재고 수량을 추가
        this.getItem().increaseStock(this.getCount());
    }

    public int calculateTotalPrice() {
        return this.getOrderPrice()*this.getCount();
    }
    /**
     * 직접 필드에 접근(ex: orderPrice*count)를 하지 않고, Getter로 필드를 접근하는 이유는 프록시 객체를 이용 했을때, 프록시 객체에서는 필드의 값이 null이 반환 됩니다.
     * 그러므로, Getter를 사용해서 프록시 객체가 아닌 원본 객체에서의 필드 값들을 가죠오게 합니다.
     */
}
