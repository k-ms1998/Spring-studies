package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpqlMain {
    public static void main(String[] args) {
//        projectionPractice();
        pagingPractice();
    }

    private static void pagingPractice() {
        /**
         * 페이징 API
         * setFirstResult(int startPosition) : 조회 시작 위치
         * setMaxResults(int maxResult) : 조회할 데이터 수
         * SELECT *FROM member ORDER BY age DESC LIMIT 5,10; ||  SELECT *FROM member ORDER BY age DESC LIMIT 10 OFFSET 5;
         */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {

            for (int i = 1; i <= 100; i++) {
                Member tmpMember = new Member();
                tmpMember.setUsername("member" + i);
                tmpMember.setAge(i + 20);
                em.persist(tmpMember);
            }

            em.flush();
            em.clear();

            // SELECT *FROM member ORDER BY age DESC LIMIT 5,10; ||  SELECT *FROM member ORDER BY age DESC LIMIT 10 OFFSET 5;
            List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(5)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("resultList.size() = " + resultList.size());
            for (Member member : resultList) {
                System.out.println("member = " + member);
            }


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally{
            em.close();
        }

        emf.close();
    }

    private static void projectionPractice() {
        /**
         * 프로젝션 - 여러 값 조회 하기
         * SELECT username, age FROM member;
         */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(25);
            em.persist(member);

            em.flush();
            em.clear();

            // 1. new 명령어로 값 조회 ==> 가장 선호되는 방법
            // MemberDTO(username, age) 생성자 호출
            List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            for (MemberDTO dto : resultList) {
                System.out.println("dto.getUsername() = " + dto.getUsername());
                System.out.println("dto.getAge() = " + dto.getAge());
            }
            /**
             * dtoId.getUsername() = member1
             * dtoId.getAge() = 25
             */

            //MemberDTO(id, username, age) 생성자 호출
            List<MemberDTO> resultListId = em.createQuery("select new jpql.MemberDTO(m.id, m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            for (MemberDTO dtoId : resultListId) {
                System.out.println("dtoId.getId() = " + dtoId.getId());
                System.out.println("dtoId.getUsername() = " + dtoId.getUsername());
                System.out.println("dtoId.getAge() = " + dtoId.getAge());
            }
            /**
             * dtoId.getId() = 1
             * dtoId.getUsername() = member1
             * dtoId.getAge() = 25
             */


            // 2. Query 타입으로 값 조회
            List resultList1 = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();
            for (Object o : resultList1) {
                Object[] oo = (Object[]) o;
                //index 1 => m.username, index 2 => m.age
                System.out.println("oo.getUsername() = "+ oo[0]);
                System.out.println("oo.getAge() = "+ oo[1]);
            }

            // 3. Object[] 타입으로 값 조회
            List<Object[]> resultList2 = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();
            for (Object[] result : resultList2) {
                //index 1 => m.username, index 2 => m.age
                System.out.println("result.getUsername() = " + result[0]);
                System.out.println("result.getAge() = " + result[1]);
            }


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally{
            em.close();
        }

        emf.close();
    }
}
