package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.practice.Movie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class JpaShopMain {
    public static void main(String[] args) {
        mainMethod();
//        practiceMethod();
    }

    public static void mainMethod() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();

        Member member = new Member();
        Order orderA = new Order();
        Order orderB = new Order();
        try {
            orderA.setMemberId(member);
            orderB.setMemberId(member);
            member.getOrderList().add(orderA);
            member.getOrderList().add(orderB);
            // add(orderA) && add(orderB)를 하지 않아도 orderList()에 추가됨 => 하단 주석 참고
            // But, add() 해주는 것이 권장됨;
            // Because, 해주는 것이 객체 지향적이고, 해주지 않으면 1차 캐시에만 값이 들어가 있을때 오류 발생 & 테스트 케이스 작성 시 오류 발생

            member.setCreatedBy("adminA");
            member.setCreatedDate(LocalDateTime.now());

            em.persist(member);
            em.persist(orderA);
            em.persist(orderB);

            em.flush();
            em.clear();

            Order findOrder = em.find(Order.class, orderA.getId());
            Member findMember = findOrder.getMemberId();
            System.out.println("findMember.getId() = " + findMember.getId());

            /**
             * member의 orderList에 orderA랑 orderB를 add() 하지 않아도 orderList에 orderA와 orderB가 자동으로 add() 된 것을 확인 할 수 있다.
             */
            List<Order> orderList = findMember.getOrderList();
            for (Order o : orderList) {
                System.out.println("o.getId() = " + o.getId());
            }


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }

    public static void practiceMethod() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Movie movie = new Movie();
            movie.setActor("actorA");
            movie.setDirector("directorA");
            movie.setName("movieA");
            movie.setPrice(15000);

            em.persist(movie);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
