package jpabook.jpashop.Service;

import jpabook.jpashop.Domain.*;
import jpabook.jpashop.Domain.item.Item;
import jpabook.jpashop.Repository.ItemRepository;
import jpabook.jpashop.Repository.MemberRepository;
import jpabook.jpashop.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //Order
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        /**
         * Order에서 orderItems, delivery는 cascade = CascadeType.ALL로 설정했기 때문에,
         * orderItem이나 delivery를 따로 repository를 만들어서 persist() 해주지 않아도 연관 관계의 order가 persis() 되면,
         * 같이 연관되어 있는 orderItem과 delivery도 persist() 됩니다
         * Cascade를 당하는 엔티티가 하나의 엔티티만 참조를 할때 사용합니다.
         */

        return order.getId();
    }

    //Cancel
    @Transactional
    public void cancel(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancelOrder();
        /**
         * cancelOrder를 통해 주문을 취소
         * 엔티티의 속성 값을 바꾸면 Dirty Checking 을 통해 변경된 값을 감지
         * 값의 변경이 감지되면 자동으로 DB에 update 쿼리문을 실행합니다
         * => JPA를 사용하면 값의 변경이 있을때마다 추가로 update 쿼리문을 개발자가 실행 할 필요가 없어집니다
         */
    }

    //Search


}
