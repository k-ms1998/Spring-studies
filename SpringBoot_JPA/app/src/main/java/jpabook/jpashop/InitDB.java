package jpabook.jpashop;

import jpabook.jpashop.Domain.*;
import jpabook.jpashop.Domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member = createMember("userA", new Address("seoul", "street", "12345"));

            Book book1 = Book.createBook("JPA1 Book", 20000, 100, "kim", "10001");
            em.persist(book1);

            Book book2 = Book.createBook("JPA2 Book", 40000, 200, "kim", "10002");
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 20);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 40);

            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB", new Address("ulsan", "blvd", "22345"));

            Book book1 = Book.createBook("Spring1 Book", 10000, 100, "lee", "20001");
            em.persist(book1);

            Book book2 = Book.createBook("Spring2 Book", 20000, 200, "lee", "20002");
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            delivery.setStatus(DeliveryStatus.READY);
            return delivery;
        }

        private Member createMember(String username, Address address) {
            Member member = new Member();
            member.setUsername(username);
            member.setAddress(address);

            em.persist(member);

            return member;
        }


    }

}
