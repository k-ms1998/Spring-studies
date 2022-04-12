package jpql;

import javax.persistence.*;
import java.util.List;

public class JpqlMain {
    public static void main(String[] args) {
//        projectionPractice();
//        pagingPractice();
//        joinPractice();
//        dataTypesPractice();
//        casePractice();
//        fetchJoinPractice1();
//        fetchJoinPractice1_1();
//        fetchJoinPractice2();
        namedQueryPractice();
    }

    private static void namedQueryPractice() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Member memberA = new Member();
            memberA.setUsername("MemberA");

            Member memberB = new Member();
            memberB.setUsername("MemberB");

            em.persist(memberA);
            em.persist(memberB);

            em.flush();
            em.clear();

            /**
             * Named 쿼리 장점:
             * 1. 미리 정의해서 이름을 부여해두고 사용하는 JPQL
             * 2. 정적 쿼리
             * 3. 애플리케이션 로딩 시점에 초기화 후 재사용
             * 4. 애플리케이션 로딩 시점에 쿼리를 검증
             */
            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("userName", "MemberA")
                    .getResultList();
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

    private static void fetchJoinPractice2() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Team teamA = new Team();
            teamA.setName("TeamA");

            Team teamB = new Team();
            teamB.setName("TeamB");

            em.persist(teamA);
            em.persist(teamB);

            Member memberA = new Member();
            memberA.setUsername("MemberA");
            memberA.setTeam(teamA); //setTeam 에서 team에 member도 추가하도록 개발했기 때문에 추가로 team.getMembers().add(team) 필요 X

            Member memberB = new Member();
            memberB.setUsername("MemberB");
            memberB.setTeam(teamA);

            Member memberC = new Member();
            memberC.setUsername("MemberC");
            memberC.setTeam(teamB);

            em.persist(memberA);
            em.persist(memberB);
            em.persist(memberC);

            em.flush();
            em.clear();

            String query = "select t from Team t join fetch t.members";
            List<Team> resultList = em.createQuery(query, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getResultList();
            for (Team team : resultList) {
                System.out.println("team.getName() = " + team.getName() +
                        "| members = " + team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }
            }
            /**
             * JOIN FETCH 랑 페이징 API(setFirstResult, setMaxResult, etc.)을 함께 사용하면 아래 같은 경고를 남김
             * WARN: HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
             */
            
            

            transaction.commit();
        } catch (Exception e) {

            transaction.rollback();
        } finally{
            em.close();
        }

        emf.close();
    }

    private static void fetchJoinPractice1_1() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Team teamA = new Team();
            teamA.setName("TeamA");

            Team teamB = new Team();
            teamB.setName("TeamB");

            em.persist(teamA);
            em.persist(teamB);

            Member memberA = new Member();
            memberA.setUsername("MemberA");
            memberA.setTeam(teamA); //setTeam 에서 team에 member도 추가하도록 개발했기 때문에 추가로 team.getMembers().add(team) 필요 X

            Member memberB = new Member();
            memberB.setUsername("MemberB");
            memberB.setTeam(teamA);

            Member memberC = new Member();
            memberC.setUsername("MemberC");
            memberC.setTeam(teamB);

            em.persist(memberA);
            em.persist(memberB);
            em.persist(memberC);

            em.flush();
            em.clear();

            /**
             * SELECT   t.*, m.* FROM Team t JOIN Member m ON t.id = m.team_id;
             */
            String queryA = "select t from Team t join fetch t.members";
            List<Team> resultListA = em.createQuery(queryA, Team.class)
                    .getResultList();
            for (Team team : resultListA) {
                System.out.println("team.getName() = " + team.getName() +
                        ", team.getMembers().size() = " + team.getMembers().size());
            }

            String queryB = "select distinct t from Team t join fetch t.members";
            List<Team> resultListB = em.createQuery(queryB, Team.class)
                    .getResultList();
            for (Team team : resultListB) {
                System.out.println("team.getName() = " + team.getName() +
                        ", team.getMembers().size() = " + team.getMembers().size());
            }

            transaction.commit();
        } catch (Exception e) {

            transaction.rollback();
        } finally{
            em.close();
        }

        emf.close();
    }

    private static void fetchJoinPractice1() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Team teamA = new Team();
            teamA.setName("TeamA");

            Team teamB = new Team();
            teamB.setName("TeamB");

            em.persist(teamA);
            em.persist(teamB);

            Member memberA = new Member();
            memberA.setUsername("MemberA");
            memberA.setTeam(teamA); //setTeam 에서 team에 member도 추가하도록 개발했기 때문에 추가로 team.getMembers().add(team) 필요 X

            Member memberB = new Member();
            memberB.setUsername("MemberB");
            memberB.setTeam(teamA);

            Member memberC = new Member();
            memberC.setUsername("MemberC");
            memberC.setTeam(teamB);

            em.persist(memberA);
            em.persist(memberB);
            em.persist(memberC);

            em.flush();
            em.clear();

            String queryA = "select m from Member m";
            List<Member> resultListA = em.createQuery(queryA, Member.class)
                    .getResultList();
            for (Member member : resultListA) {
                System.out.println("member.getUsername() = " + member.getUsername()
                        + ", member.getTeam().getName() = " + member.getTeam().getName());
                /**
                 * RESULT:
                 *     select
                 *         team0_.id as id1_3_0_,
                 *         team0_.name as name2_3_0_
                 *     from
                 *         Team team0_
                 *     where
                 *         team0_.id=?
                 * member.getUsername() = MemberA, member.getTeam().getName() = TeamA
                 * member.getUsername() = MemberB, member.getTeam().getName() = TeamA
                 * Hibernate:
                 *     select
                 *         team0_.id as id1_3_0_,
                 *         team0_.name as name2_3_0_
                 *     from
                 *         Team team0_
                 *     where
                 *         team0_.id=?
                 * member.getUsername() = MemberC, member.getTeam().getName() = TeamB
                 *
                 * MemberA, teamA => DB => 영속성 컨텍스트에 teamA가 없기 때문에 SQL문으로 DB에서 가져옴
                 * MemberB, teamA => 1차 캐시 => MemberA때 teamA가 영속성 컨텍스트에 추가 됐기 때문에 영속성 컨텍스트/1차 캐시에서 가져옴
                 * MemberC, teamB => DB => 양속성 컨텍스트에 teamB가 없기 때문에 SQL문으로 DB에서 가져옴
                 * 그러므로, 만약에 각 회원이 각각 다른팀에 소속되어 있으면, 팀의 갯수 만큼 쿼리문이 날라감 => 성능 저하, N+1
                 * 이런 문제점을 해결하기 위해 fetch join 사용
                 */
            }

            String queryB = "select m from Member m join fetch m.team";
            List<Member> resultListB = em.createQuery(queryB, Member.class)
                    .getResultList();
            for (Member member : resultListB) {
                System.out.println("memberJoinFetch.getUsername() = " + member.getUsername()
                        + ", memberJoinFetch.getTeam().getName() = " + member.getTeam().getName());
                /**
                 * RESULT:
                 * Hibernate:
                 *     /* select
                 *         m
                 *     from
                 *         Member m
                 *     join
                 *         fetch m.team *\/select
                 *          member0_.id as id1_0_0_,
                 *          team1_.id as id1_3_1_,
                 *          member0_.age as age2_0_0_,
                 *          member0_.MEMBER_TYPE as MEMBER_T3_0_0_,
                 *          member0_.TEAM_ID as TEAM_ID5_0_0_,
                 *          member0_.username as username4_0_0_,
                 *          team1_.name as name2_3_1_
                 *          from
                 *              Member member0_
                 *          inner join
                 *              Team team1_
                 *                  on member0_.TEAM_ID = team1_.id
                 * memberJoinFetch.getUsername() = MemberA, memberJoinFetch.getTeam().getName() = TeamA
                 * memberJoinFetch.getUsername() = MemberB, memberJoinFetch.getTeam().getName() = TeamA
                 * memberJoinFetch.getUsername() = MemberC, memberJoinFetch.getTeam().getName() = TeamB
                 * 
                 * 프록시 X, 실제로 member와 team을 inner join한 값을 resultListB에 저장
                 */
            }

            transaction.commit();
        } catch (Exception e) {

            transaction.rollback();
        } finally{
            em.close();
        }

        emf.close();
    }

    private static void casePractice() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Team teamA = new Team();
            teamA.setName("TeamA");

            Team teamB = new Team();
            teamB.setName("TeamB");

            em.persist(teamA);
            em.persist(teamB);

            for (int i = 5; i <= 20; i++) {
                Member tmpMember = new Member();
                if (i % 3 == 0) {
                    tmpMember.setUsername("member" + i);
                }

                tmpMember.setAge(i + 10);
                if (i % 2 == 0) {
                    tmpMember.setTeam(teamA);
                    tmpMember.setMemberType(MemberType.ADMIN);
                } else {
                    tmpMember.setTeam(teamB);
                    tmpMember.setMemberType(MemberType.USER);
                }

                em.persist(tmpMember);
            }

            em.flush();
            em.clear();

            /**
             * case
             * when
             * end
             */
            String queryA = "select" +
                        " case when m.age <= 20 then '청소년'" +
                        " when m.age <= 25 then '청년'" +
                        " else '성인'" +
                        " end" +
                    " from Member m";
            List<String> resultListA = em.createQuery(queryA, String.class)
                    .getResultList();
            for (String s : resultListA) {
                System.out.println("s.case = " + s);
            }

            /**
             * 사용자 이름이 없으면 '이름 미입력' 반환
             */
            String queryB = "select coalesce(m.username, '이름 미입력') from Member m";
            List<String> resultListB = em.createQuery(queryB, String.class)
                    .getResultList();
            for (String s : resultListB) {
                System.out.println("s.coalesce = " + s);
            }

            /**
             * 사용자 나이가 20이면 null을 반환하고, 나머지는 본인의 나이를 반환
             */
            String queryC = "select NULLIF(m.age, :age) from Member m";
            List<Integer> resultListC = em.createQuery(queryC, Integer.class)
                    .setParameter("age", 20)
                    .getResultList();
            System.out.println("resultListC.size() = " + resultListC.size());
            for (Integer s : resultListC) {
                System.out.println("s.nullif = " + s);
            }

            transaction.commit();
        } catch (Exception e) {

            transaction.rollback();
        } finally{
            em.close();
        }

        emf.close();
    }

    private static void dataTypesPractice() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Team teamA = new Team();
            teamA.setName("TeamA");

            Team teamB = new Team();
            teamB.setName("TeamB");

            em.persist(teamA);
            em.persist(teamB);

            for (int i = 1; i <= 10; i++) {
                Member tmpMember = new Member();
                if (i % 3 == 0) {
                    tmpMember.setUsername("member" + i);
                }
                else{
                    tmpMember.setUsername("user" + i);
                }
                tmpMember.setAge(i + 20);
                if (i % 2 == 0) {
                    tmpMember.setTeam(teamA);
                    tmpMember.setMemberType(MemberType.ADMIN);
                } else {
                    tmpMember.setTeam(teamB);
                    tmpMember.setMemberType(MemberType.USER);
                }

                em.persist(tmpMember);
            }

            em.flush();
            em.clear();

            /**
             * new MemberDto(Long id, MemberType memberType)
             */
            String query = "select new jpql.MemberDTO(m.id, m.age, m.memberType) from Member m" +
                    " where m.memberType = :memType" +
                    " and m.age between 20 and 25" +
                    " and m.username like :userName";
            List<MemberDTO> resultList = em.createQuery(query, MemberDTO.class)
                    .setParameter("memType", MemberType.USER)
                    .setParameter("userName", "%user%")
                    .getResultList();
            for (MemberDTO memberDTO : resultList) {
                System.out.println("memberDTO.getAge() = " + memberDTO.getAge()
                        + ", memberDTO.getMemberType() = " + memberDTO.getMemberType());
            }


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally{
            em.close();
        }

        emf.close();

    }

    private static void joinPractice() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            Team teamA = new Team();
            teamA.setName("TeamA");

            Team teamB = new Team();
            teamB.setName("TeamB");

            em.persist(teamA);
            em.persist(teamB);

            for (int i = 1; i <= 10; i++) {
                Member tmpMember = new Member();
                tmpMember.setUsername("member" + i);
                tmpMember.setAge(i + 20);
                if (i % 2 == 0) {
                    tmpMember.setTeam(teamA);
                } else {
                    tmpMember.setTeam(teamB);
                }

                em.persist(tmpMember);
            }

            em.flush();
            em.clear();

            /**
             * SELECT m.*, t.name FROM member m LEFT JOIN team t ON m.team_id = t.id WHERE t.name="TeamA";
             */
            String query = "select m from Member m left join m.team t where t.name=:teamName";
            List<Member> resultList = em.createQuery(query, Member.class)
                    .setParameter("teamName", "TeamA")
                    .getResultList();

            for (Member member : resultList) {
                System.out.println("member.getAge() = " + member.getAge() + ", member.getTeam() = " + member.getTeam().getName()
                        + ", member.getTeam().getId() = " + member.getTeam().getId());
            }

            String query2 = "select m from Member m left join Team t on m.id = t.id where t.name=:teamName";
            List<Member> resultList2 = em.createQuery(query2, Member.class)
                    .setParameter("teamName", "TeamA")
                    .getResultList();

            for (Member member2 : resultList2) {
                System.out.println("member2.getAge() = " + member2.getAge() + ", member2.getTeam() = " + member2.getTeam().getName()
                        + ", member2.getTeam().getId() = " + member2.getTeam().getId());
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally{
            em.close();
        }

        emf.close();
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
