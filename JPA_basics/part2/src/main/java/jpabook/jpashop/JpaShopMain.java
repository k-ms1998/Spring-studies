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
//        mainMethod();
//        practiceMethod();
        proxyPracticeMethod();
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

    private static void proxyPracticeMethod() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        /**
         * 영속성 컨텍스트에 member1 엔티티가 존재 할때
         */
        try {
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember.getClass() = " + refMember.getClass()); //Proxy

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember.getClass() = " + findMember.getClass()); //Member

            System.out.println("findMember == refMember: " + (findMember == refMember));

            /**
             * refMember.getClass() = class jpabook.jpashop.domain.Member
             * findMember.getClass() = class jpabook.jpashop.domain.Member
             * findMember == refMember: true
             *
             * commit 전이기 때문에 영속성 컨텍스트에 member1 엔티티가 존재하고 있음
             * 그러므로, getReference()로 프록시를 가져와도 class jpabook.jpashop.domain.Member가 반환됨
             */

        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 영속성 컨텍스트에 member1 엔티티가 존재하지 않을때
         */
        try {
            transaction.begin();
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember.getClass() = " + refMember.getClass());

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember.getClass() = " + findMember.getClass());

            System.out.println("findMember == refMember: " + (findMember == refMember));
            /**
             * refMember.getClass() = class jpabook.jpashop.domain.Member$HibernateProxy$y6v2GoZr
             * findMember.getClass() = class jpabook.jpashop.domain.Member$HibernateProxy$y6v2GoZr
             * findMember == refMember: true
             *
             * em.flush() && em.clear()로 영속성 컨텍스트에서 member1를 없앴음.
             * 하지만, refMember가 getReference()로 프록시 객체가 Member를 상속 받아 메모리에 할당 됨.
             * 메모리에 할당 됨으로, 지연 로딩에 의해 refMember.getClass()를 실행해도 오류 X.
             * 이떄, member1 객체는 없고, , member1의 프록시인 refMember만 존재
             * 그러므로, getReference()로 프록시를 가져오면 class jpabook.jpashop.domain.Member$HibernateProxy$y6v2GoZr 가 반환됨.
             * 이때, find()를 통해 엔티티를 가져와도, getReference()로 반한된 값이랑 같아야하기 때문에 find()도 동일하게
             * class jpabook.jpashop.domain.Member$HibernateProxy$y6v2GoZr가 반환됩니다.
             */
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }

        /**
         * 영속성 컨텍스트에 member1 엔티티와 프록시 모두 없을때
         */
        try {
            transaction.begin();
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember.getClass() = " + refMember.getClass());

            em.flush();
            em.clear(); //영속성 컨텍스트를 깨끗하게 지워버림

            System.out.println("refMember.getName(): " +  refMember.getName());
            /**
             * org.hibernate.LazyInitializationException 오류 발생
             *
             * 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 Exception 발생
             *
             * System.out.println("refMember.getClass() = " + refMember.getClass()); 이후, em.clear()를 통해 영속성 컨텍스트를 깨끗하게 만들어버림
             * 이후, 앞서 본 예제들과는 달리, refMember.getReference()를 통해 프록시 객체를 받아서 메모리에 할당되지 않은 상태에서 바로 refMember.getName() 호출
             * 그러므로, org.hibernate.LazyInitializationException 발생
             */

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }

        em.close();
        emf.close();

    }
}
