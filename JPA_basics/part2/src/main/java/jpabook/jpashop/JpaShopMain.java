package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaShopMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();

        Member member = new Member();
        Order order = new Order();
        try {
            order.setMemberId(member);
            em.persist(member);
            em.persist(order);

            Order findOrder = em.find(Order.class, order.getId());
            Member findMember = findOrder.getMemberId();
            System.out.println("findMember.getId() = " + findMember.getId());


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }


        emf.close();
    }
}
