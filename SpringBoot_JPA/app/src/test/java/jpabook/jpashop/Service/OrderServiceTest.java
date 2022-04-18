package jpabook.jpashop.Service;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderStatus;
import jpabook.jpashop.Domain.item.Book;
import jpabook.jpashop.Exception.NotEnoughStockException;
import jpabook.jpashop.Repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void 상품주문() {
        //given
        Member member = createMember();
        Book book = createBook();

        //when
        int orderCnt = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCnt);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER, findOrder.getStatus(), "상품 주문시 상태는 ORDER");
        Assertions.assertEquals(1, findOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        Assertions.assertEquals(10000 * 2, findOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
        Assertions.assertEquals(8, book.getQuantity(), "주문 수량만큼 재고가 줄어야 한다.");

    }



    @Test
    void 주문취소() {
        //given
        Member member = createMember();
        Book book = createBook();

        int orderCnt = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCnt);

        //when
        Assertions.assertEquals(8, book.getQuantity(), "주문 취소 전에는 수량이 8");
        orderService.cancel(orderId);

        //then
        Order findOrder = orderRepository.findOne(orderId);
        Assertions.assertEquals(OrderStatus.CANCEL, findOrder.getStatus(), "주문 취소시 상태는 CANCEL");
        Assertions.assertEquals(10, book.getQuantity(), "주문 취소시 수량은 10");
    }

    @Test
    void 재고수량초과() {
        //given
        Member member = createMember();
        Book book = createBook();

        //when
        int orderCnt = 11;

        //then
        Assertions.assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), book.getId(), orderCnt);
            // OrderService.order() -> OrderItem.createOrderItem() -> Item.decreaseStock() -> NotEnoughStockException
        });
    }

    private Book createBook() {
        Book book = new Book();
        book.setName("JPA");
        book.setPrice(10000);
        book.setQuantity(10);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setUsername("MemberA");
        member.setAddress(new Address("seoul", "blvd", "12345"));
        em.persist(member);
        return member;
    }

}