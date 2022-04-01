package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); // /persistence.xml => <persistence-unit name="hello">
        EntityManager em = emf.createEntityManager(); //테이블 접근 시, 무조건 EntityManager 을 통해서 해야됨

        EntityTransaction transaction = em.getTransaction(); //JPA 에서의 모든 데이터 변경을 EntityTransaction 으로 이루어짐

        transaction.begin(); // 데이터베이스 transaction 을 시작
        em.createQuery("delete from Member").executeUpdate(); //실행 할때마다 테이블의 모든 튜플들 삭제
        transaction.commit();


        //Create
        Member memberA = new Member();
        memberA.setId(1L);
        memberA.setName("hello"); // Member 테이블에 저장할 튜플 생성

        Member memberB = new Member();
        memberB.setId(2L);
        memberB.setName("helloB"); // Member 테이블에 저장할 튜플 생성

        try {
            transaction.begin(); // 데이터베이스 transaction 을 시작; 이전에 transaction.commit()을 했으면 다시 begin()을 해줘야 함

            /**
             * Member 테이블에 memberA && memberB 객체들 적재
             */
            em.persist(memberA);
            em.persist(memberB);
            

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }


        //Remove
        try {
            transaction.begin(); // 데이터베이스 transaction 을 시작

            /**
             * Column id로 원하는 튜플을 찾아서 객체로 반환 받고, 해당 객체를 제거
             */
            Member findMember = em.find(Member.class, 1L);
            em.remove(findMember);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
        
        //Update
        try {
            transaction.begin(); // 데이터베이스 transaction 을 시작

            /**
             * Column id로 원하는 튜플을 찾아서 객체로 반환 받고, 해당 객체의 값을 수정
             */
            Member findMember = em.find(Member.class, 1L);
            findMember.setName("helloJPA"); // persist()을 하지 않아도 자동으로 테이블이 업데이트 됨

            transaction.commit(); //commit을 통해 변화 반영
        } catch (Exception e) {
            transaction.rollback();
        }


        try {
            /**
             * 쿼리문으로 원하는 값 찾기
             * select *from member
             */
            List<Member> memberList = em.createQuery("select m from Member as m", Member.class)
                    .getResultList();
            for (Member mem : memberList) {
                System.out.println("member.name = " + mem.getName());
            }

        } catch (Exception e) {

        }

        //항상 EntityManager랑 EntityManagerFactory를 close() 해줌
        em.close(); 
        emf.close();
    }
}
