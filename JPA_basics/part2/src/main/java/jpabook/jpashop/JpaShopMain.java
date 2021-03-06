package jpabook.jpashop;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.AddressEntity;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.practice.Movie;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class JpaShopMain {
    public static void main(String[] args) {
//        mainMethod();
//        practiceMethod();
//        proxyPracticeMethod();
//        practiceLazy();
//        cascadePractice();
//        embeddedPractice();
//        elementCollectionPractice();
        criteriaAndNativePractice();
    }

    private static void criteriaAndNativePractice() {
        /**
         *   Criteria:
         *   문자가 아닌 자바코드로 JPQL을 작성할 수 있음
         * • JPQL 빌더 역할
         * • JPA 공식 기능
         * • 단점: 너무 복잡하고 실용성이 없다.
         * • => Criteria 대신에 QueryDSL 사용 권장
         */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Member memberA = new Member();
            memberA.setName("memberA");

            Member memberB = new Member();
            memberB.setName("memberB");

            Member memberC = new Member();
            memberC.setName("userC");

            em.persist(memberA);
            em.persist(memberB);
            em.persist(memberC);

            em.flush();
            em.clear();

            //Criteria 사용 준비
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);



            System.out.println("==============QUERY===============");
            /**
             *  SELECT *FROM Member m WHERE m.name like "%member%";
             */
            Root<Member> m = query.from(Member.class);
            CriteriaQuery<Member> criteriaQuery = query.select(m).where(cb.like(m.get("name"), "%member%"));
            List<Member> resultList = em.createQuery(criteriaQuery)
                    .getResultList();

            for (Member member : resultList) {
                System.out.println("member.getName() = " + member.getName());
            }

            //Native Query
            String q = "SELECT *FROM member m WHERE m.name like '%member%'";
            List<Member> resultList1 = em.createNativeQuery(q, Member.class)
                    .getResultList();
            for (Member nativeMember : resultList1) {
                System.out.println("nativeMember.getName() = " + nativeMember.getName());
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }
        
        emf.close();

    }

    private static void elementCollectionPractice() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Member member = new Member();
            member.setName("memberA");
            member.setHomeAddress(new Address("seoul", "blvd", "12345"));

            //값 타입 저장
            member.getFavoriteFoods().add("Chicken");
            member.getFavoriteFoods().add("Pizza");
            member.getFavoriteFoods().add("Soda");

            member.getAddressHistory().add(new AddressEntity(new Address("incheon", "street", "12346")));
            member.getAddressHistory().add(new AddressEntity(new Address("busan", "ave", "12347")));

            Member member2 = new Member();
            member.setName("memberB");
            member.setHomeAddress(new Address("seoul", "blvd", "22345"));

            member2.getFavoriteFoods().add("Pizza");

            member2.getAddressHistory().add(new AddressEntity(new Address("ulsan", "street", "22346")));

            em.persist(member);
            em.persist(member2);

            em.flush();
            em.clear();


            Member findMember1 = em.find(Member.class, member.getId());
            // 값 타입 조회
            List<AddressEntity> addressHistory1 = findMember1.getAddressHistory();
            for (AddressEntity address : addressHistory1) {
                System.out.println("address = " + address.getAddress().getCity());
            }
            Set<String> favoriteFoods1 = findMember1.getFavoriteFoods();
            for (String s : favoriteFoods1) {
                System.out.println("s = " + s);
            }

            //값 타입 수정
            /**
             * findMember1이 같은 city, 같은 street에서 zipcode만 다른 곳으로 이사 갔다고 가정했을때,
             * 현 주소를 prev_address 테이블에 추가하고, zipcode만 바꾸고 싶은 상황
             * 이때, Address는 값 타입 컬렉션이기 때문에, zipcode만 수정하면 안되고, Address 임베디드 타입을 전체를 새로 저장시켜 줘야됨
             * => findMember1.setHomeAddress(new Address(oldAddress.getCity(), oldAddress.getStreet(), "77777"));
             */
            Address oldAddress = findMember1.getHomeAddress();
            findMember1.getAddressHistory().add(new AddressEntity(oldAddress));
            findMember1.setHomeAddress(new Address(oldAddress.getCity(), oldAddress.getStreet(), "77777"));

            //Pizza -> Pasta로 바꾸고 싶을때 => 해당 값을 삭제하고 새로 추가해 줘야됨
            findMember1.getFavoriteFoods().remove("Pizza");
            findMember1.getFavoriteFoods().add("Pasta");


            /**
             * Address 컬렉션에 저장된 값들을 비교해서, 모든 값들이 일치하면 같은 객체이다
             * 이떄, Address에서 equals()를 오버라이딩 해서, 모든 값들(city, street, zipcode)이 같으면 같은 객체라고 인식하도록 코딩 해줘야 한다.
             */
            findMember1.getAddressHistory().remove(new AddressEntity(new Address("busan", "ave", "12347")));

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }

        emf.close();

    }

    private static void embeddedPractice() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Member member = new Member();
            member.setName("memberA");
            member.setHomeAddress(new Address("seoul", "blvd", "12345"));

            Address oldAddress = member.getHomeAddress();
            member.setHomeAddress(new Address(oldAddress.getCity(), oldAddress.getStreet(), "77777"));

            em.persist(member);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }

    private static void cascadePractice() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Member member = new Member();
            member.setName("memberA");

            Order orderA = new Order();
            Order orderB = new Order();

            orderA.setMemberId(member);
            orderB.setMemberId(member);

            member.getOrderList().add(orderA);
            member.getOrderList().add(orderB);

            em.persist(member);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }

        emf.close();

    }

    private static void practiceLazy() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            Order order1 = new Order();
            order1.setOrderDate(LocalDateTime.now());
            order1.setMemberId(member1);
            em.persist(order1);

            em.flush();
            em.clear();

            Order findOrder = em.find(Order.class, order1.getId());
            System.out.println("findOrder.getClass() = " + findOrder.getMemberId().getClass());

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }

        emf.close();



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
