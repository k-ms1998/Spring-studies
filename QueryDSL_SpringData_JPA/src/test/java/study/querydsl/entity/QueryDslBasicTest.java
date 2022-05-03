package study.querydsl.entity;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
public class QueryDslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory factory;

    @BeforeEach
    public void before() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("MemberA", 25, teamA);
        Member memberB = new Member("MemberB", 35, teamA);
        Member memberC = new Member("MemberC", 45, teamA);
        Member memberD = new Member("MemberD", 55, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);
        
    }
    
    @Test
    void startJPQL() {
        //Find memberA
        String query = "select m from Member m where m.username = :username and age >= :age";
        Member findMemberA = em.createQuery(query, Member.class)
                .setParameter("username", "MemberA")
                .setParameter("age", 25)
                .getSingleResult();

        Assertions.assertThat(findMemberA.getUsername()).isEqualTo("MemberA");

    }

    @Test
    void startQueryDSL() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        // select *from member where username = 'MemberA' and age >= 25;
        Member findMemberA = factory.select(member)
                .from(member)
                .where(member.username.eq("MemberA") ,
                        member.age.goe(25))   // || .where(member.username.eq("MemberA").and(member.age.goe(25)))
                .fetchOne();

        Assertions.assertThat(findMemberA.getUsername()).isEqualTo("MemberA");

    }

    @Test
    void queryDslProjectionBasic() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        QTeam team = QTeam.team;


        //fetch() -> 리스트 반환; 데이터가 없으면 Empty List 반환
        List<Member> membersFetch = factory
                .selectFrom(member)
                .fetch();

        //fetchOne() -> 단건 조회: 결과가 없으면 == null, 결과가 2개 이상 == NonUniqueResultException
        Team teamFetchOne = factory
                .selectFrom(team)
                .fetchOne();

        //fetchOne() -> limit(1).fetchOne()
        Member membersFetchFirst = factory
                .selectFrom(member)
                .fetchFirst();

        //fetchResult() && fetchCount() deprecated -> solution
        Long memberCount = factory
                .select(member.count())
                .from(member)
                .fetchOne();

        Assertions.assertThat(membersFetch.size()).isEqualTo(4);
        Assertions.assertThat(teamFetchOne.getName()).isEqualTo("TeamA");
        Assertions.assertThat(membersFetchFirst).isInstanceOf(Member.class);
        Assertions.assertThat(memberCount).isEqualTo(4);
    }

    @Test
    void queryDslOrderBy() {
        /**
         * 회원 정렬 순서:
         * 1. 회원 나이 내림차순 DESC
         * 2. 회원 이름 오름차순 ASC
         * => select *from member order by age desc, username asc;
         */
        em.persist(new Member("MemberE", 35));
        em.persist(new Member(null, 35));

        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        List<Member> members = factory
                .select(member)
                .from(member)
                .orderBy(member.age.desc(), member.username.asc().nullsLast()) // nullsLast() -> null일 경우 마지막 순서로 가져옴; nullsFirst() -> null일 경우 처음으로 가져옴
                .fetch();
        for (Member m : members) {
            System.out.println("m = " + m);
        }


    }

    @Test
    void queryDslPaging() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        List<Member> members = factory.selectFrom(member)
                .orderBy(member.age.desc())
                .offset(1)
                .limit(2)
                .fetch();

        for (Member m : members) {
            System.out.println("m = " + m);
        }
        Assertions.assertThat(members.size()).isEqualTo(2);
        Assertions.assertThat(members.get(0).getUsername()).isEqualTo("MemberC");
    }

    @Test
    void aggregation() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        List<Tuple> result = factory.select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                ).from(member)
                .fetch(); // 어차피 반환되는 튜플은 하나이므로, fetchOne() || fetchFirst() 를 사용해도 무관
        //반환되는 Column의 값들이 타입이 다양하기 때문에 Tuple 로 반환; 실무에서는 DTO로 바로 반환
        
        Tuple tuple = result.get(0);

        System.out.println("tuple = " + tuple); //tuple = [4, 160, 40.0, 55, 25]

        Assertions.assertThat(tuple.get(member.count())).isEqualTo(4);
        Assertions.assertThat(tuple.get(member.age.sum())).isEqualTo(160);
        Assertions.assertThat(tuple.get(member.age.avg())).isEqualTo(40);
        Assertions.assertThat(tuple.get(member.age.max())).isEqualTo(55);
        Assertions.assertThat(tuple.get(member.age.min())).isEqualTo(25);


    }

    @Test
    void groupBy() {
        factory = new JPAQueryFactory(em);
        QTeam team = QTeam.team;
        QMember member = QMember.member;

        List<Tuple> result = factory.select(team.name, member.age.avg(), member.count())
                .from(member)
                .join(member.team, team) // member LEFT JOIN team
                .groupBy(team.name)
                .having(member.age.avg().goe(25)) // having도 사용 가능
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

        Tuple tupleA = result.get(0); //tuple = [TeamA, 35.0, 3]
        Tuple tupleB = result.get(1); //tuple = [TeamB, 55.0, 1]

        Assertions.assertThat(result.size()).isEqualTo(2);
//        Assertions.assertThat(tupleA.get(member.age.avg())).isEqualTo(35);
//        Assertions.assertThat(tupleB.get(member.age.avg())).isEqualTo(55);

    }

    @Test
    void join1() {
        factory = new JPAQueryFactory(em);
        QTeam team = QTeam.team;
        QMember member = QMember.member;

        /**
         * select member0_.member_id as member_i1_0_, member0_.created_date as created_2_0_, member0_.last_modified_date as last_mod3_0_, member0_.age as age4_0_, member0_.team_id as team_id6_0_, member0_.username as username5_0_
         * from member member0_
         * inner join team team1_
         * on member0_.team_id=team1_.team_id
         * where team1_.name='TeamA';
         */
        List<Member> members = factory.selectFrom(member)
                .join(member.team, team) // join() == innerJoin(); leftJoin, rightJoin()도 가능
                .where(team.name.eq("TeamA"))
                .fetch();
        for (Member m : members) {
            System.out.println("m = " + m);
        }

        Assertions.assertThat(members)
                .extracting("username")
                .containsExactly("MemberA", "MemberB", "MemberC");
    }

    @Test
    void join2() {
        factory = new JPAQueryFactory(em);
        QTeam team = QTeam.team;
        QMember member = QMember.member;

        /**
         * select member0_.member_id as member_i1_0_0_, team1_.team_id as team_id1_1_1_, member0_.created_date as created_2_0_0_, member0_.last_modified_date as last_mod3_0_0_, member0_.age as age4_0_0_, member0_.team_id as team_id6_0_0_, member0_.username as username5_0_0_, team1_.created_date as created_2_1_1_, team1_.last_modified_date as last_mod3_1_1_, team1_.name as name4_1_1_
         * from member member0_
         * left outer join team team1_
         * on member0_.team_id=team1_.team_id and (team1_.name='TeamA');
         *
         * == SELECT m.*, t.* FROM member m LEFT JOIN team t ON m.team_id=t.id AND t.name = 'TeamA';
         */
        List<Tuple> resultLeftJoin = factory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("TeamA"))
                .fetch();
        for (Tuple tuple : resultLeftJoin) {
            System.out.println("tuple = " + tuple);
        }
//        select에서 member, team 모두 가져오도록 했을때
//        tuple = [Member(id=3, username=MemberA, age=25), Team(id=1, name=TeamA)]
//        tuple = [Member(id=4, username=MemberB, age=35), Team(id=1, name=TeamA)]
//        tuple = [Member(id=5, username=MemberC, age=45), Team(id=1, name=TeamA)]
//        tuple = [Member(id=6, username=MemberD, age=55), null] -> TeamB와 연관되어 있으므로, TeamB에 대한 데이터는 가져오지 못했기 때문에 Team에 대한 값은 NUL; But, Member에 대한 값은 가져옴


        /**
         * Left Join vs. Inner Join
         */
        List<Tuple> resultInnerJoin = factory
                .select(member, team)
                .from(member)
                .join(member.team, team)
                .on(team.name.eq("TeamA"))
                .fetch();
        for (Tuple tuple : resultInnerJoin) {
            System.out.println("tuple = " + tuple);
        }
//        tuple = [Member(id=3, username=MemberA, age=25), Team(id=1, name=TeamA)]
//        tuple = [Member(id=4, username=MemberB, age=35), Team(id=1, name=TeamA)]
//        tuple = [Member(id=5, username=MemberC, age=45), Team(id=1, name=TeamA)]
//        => TeamA 와 연관되어 있는 값들만 가져오고, 연관이 없는 Member의 경우 가져오지 않는다
//        Inner Join의 경우 on()을 사용하지 않고 where() 만으로도 필터링 가능

        Assertions.assertThat(resultLeftJoin.size()).isEqualTo(4);
        Assertions.assertThat(resultInnerJoin.size()).isEqualTo(3);
    }

    /**
     * 연관관계가 성립되어 있지 않은 두 테이블의 외부 조인
     * 예제:
     * Member에는 회원 정보 저장 && MemberColor에는 이름과 좋아하는 색깔을 저장
     * 각 테이블은 서로 연관되어 있지 않으며, 완전히 별개의 서비스에서 각각의 테이블을 사용 중
     * But, 요구사항에 의해서 각 Member의 정보와 좋아하는 색깔을 같이 알아야 할때 JOIN
     */
    @Test
    void joinNoRelation() {
        MemberColor m1 = new MemberColor("MemberA", "Blue");
        MemberColor m2 = new MemberColor("MemberA", "Read");
        MemberColor m3 = new MemberColor("MemberB", "Purple");
        MemberColor m4 = new MemberColor("MemberB", "Cyan");

        em.persist(m1);
        em.persist(m2);
        em.persist(m3);
        em.persist(m4);

        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        QMemberColor color = QMemberColor.memberColor;
        /**
         * SELECT *FROM member m LEFT JOIN membercolor c ON m.username = c.name;
         */
        List<Tuple> resultLeftJoin = factory
                .select(member, color)
                .from(member)
                .leftJoin(color)
                .on(member.username.eq(color.name))
                .fetch();
        for (Tuple tuple : resultLeftJoin) {
            System.out.println("tuple = " + tuple);
        }
//        tuple = [Member(id=3, username=MemberA, age=25), MemberColor(name=MemberA, color=Blue)]
//        tuple = [Member(id=3, username=MemberA, age=25), MemberColor(name=MemberA, color=Read)]
//        tuple = [Member(id=4, username=MemberB, age=35), MemberColor(name=MemberB, color=Purple)]
//        tuple = [Member(id=4, username=MemberB, age=35), MemberColor(name=MemberB, color=Cyan)]
//        tuple = [Member(id=5, username=MemberC, age=45), null]
//        tuple = [Member(id=6, username=MemberD, age=55), null]

        /**
         * SELECT *FROM member m JOIN membercolor c ON m.username = c.username;
         */
        List<Tuple> resultInnerJoin = factory
                .select(member, color)
                .from(member)
                .join(color)
                .on(member.username.eq(color.name))
                .fetch();
        for (Tuple tuple : resultInnerJoin) {
            System.out.println("tuple = " + tuple);
        }
//        tuple = [Member(id=3, username=MemberA, age=25), MemberColor(name=MemberA, color=Blue)]
//        tuple = [Member(id=3, username=MemberA, age=25), MemberColor(name=MemberA, color=Read)]
//        tuple = [Member(id=4, username=MemberB, age=35), MemberColor(name=MemberB, color=Purple)]
//        tuple = [Member(id=4, username=MemberB, age=35), MemberColor(name=MemberB, color=Cyan)]

        Assertions.assertThat(resultLeftJoin.size()).isEqualTo(6);
        Assertions.assertThat(resultInnerJoin.size()).isEqualTo(4);

    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    void fetchJoinNo() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        em.flush();
        em.clear();

        Member findMember = factory
                .selectFrom(member)
                .fetchFirst();

        boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam()); //로딩이 된 엔티티인지 아닌지 알려줌
        Assertions.assertThat(isLoaded).isFalse();
    }

    @Test
    void fetchJoinYes() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        em.flush();
        em.clear();

        Member findMember = factory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .fetchFirst();

        boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam()); //로딩이 된 엔티티인지 아닌지 알려줌
        Assertions.assertThat(isLoaded).isTrue();
    }

    @Test
    void joinAndFetchJoin() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        em.flush();
        em.clear();
        System.out.println("==========================================================");

        Member findMemberFetchJoin = factory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .fetchFirst();
        Team fetchJoinTeam = findMemberFetchJoin.getTeam();
        System.out.println("fetchJoinTeam = " + fetchJoinTeam);

        em.flush();
        em.clear();
        System.out.println("==========================================================");

        Member findMemberJoinA = factory
                .select(member)
                .from(member)
                .join(member.team, team)
                .fetchFirst();
        Team findTeamA = findMemberJoinA.getTeam();
        System.out.println("findTeamA = " + findTeamA);

        em.flush();
        em.clear();
        System.out.println("==========================================================");

        Tuple findMemberJoinB = factory
                .select(member, team)
                .from(member)
                .join(member.team, team)
                .fetchFirst();
        Team findTeamB = findMemberJoinB.get(1, Team.class);
        System.out.println("findTeamB = " + findTeamB);


    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    void subQuery() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        QMember subMember = new QMember("sub"); // 서브쿼리 안에 있는 Member는 Alias 를 다르게 해줘야 하기 때문에 new QMember(name) 으로 Alias를 설정해서 생성

        /**
         * SELECT *FROM member WHERE age = (SELECT max(sub.age) FROM member sub);
         */
        List<Member> result = factory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(subMember.age.max())
                                .from(subMember)
                ))
                .fetch();
        //eq 외 gt, goe 등등 모두 사용 가능

        Assertions.assertThat(result).extracting("age")
                .containsExactly(55);

    }

    /**
     * 서브쿼리와 IN 절을 같이 사용하기
     */
    @Test
    void subQueryIn() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        QMember subMember = new QMember("sub"); // 서브쿼리 안에 있는 Member는 Alias 를 다르게 해줘야 하기 때문에 new QMember(name) 으로 Alias를 설정해서 생성

        /**
         * SELECT * FROM member WHERE age IN (SELECT sub.age FROM member sub WHERE sub.age > 40);
         */
        List<Member> result = factory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(subMember.age)
                                .from(subMember)
                                .where(subMember.age.gt(40))
                ))
                .fetch();

        Assertions.assertThat(result).extracting("age")
                .containsExactly(45, 55);

    }

    /**
     * SELECT에서 서브쿼리를 사용하기
     */
    @Test
    void subQuerySelect() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        QMember subMember = new QMember("sub"); // 서브쿼리 안에 있는 Member는 Alias 를 다르게 해줘야 하기 때문에 new QMember(name) 으로 Alias를 설정해서 생성

        /**
         * SELECT username, avg(age) FROM MEMBER -> 쿼리문 실행 X -> 서브쿼리로 해결
         * -> SELECT member.username, (SELECT avg(sub.age) FROM member sub) FROM member;
         */
        List<Tuple> result = factory.select(member.username,
                        JPAExpressions
                                .select(subMember.age.avg())
                                .from(subMember))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }


        Assertions.assertThat(result.get(0).get(1, Double.class)).isEqualTo(40);
    }

    @Test
    void basicCase() {
        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        List<String> result = factory
                .select(member.age
                        .when(25).then("25살")
                        .when(35).then("35살")
                        .when(45).then("45살")
                        .otherwise("46살 이상"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
//        s = 25살
//        s = 35살
//        s = 45살
//        s = 46살 이상

    }

    @Test
    void complexCase() {
        Member memberE = new Member("MemberE", 49, null);
        em.persist(memberE);

        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;


        List<String> result = factory
                .select(new CaseBuilder()
                        .when(member.age.between(20, 29)).then("20대") //20 <= age <= 29
                        .when(member.age.between(30, 39)).then("30대")
                        .when(member.age.between(40, 49)).then("40대")
                        .otherwise("50대 이상"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void complexCaseOrderBy() {
        Member memberE = new Member("MemberE", 49, null);
        em.persist(memberE);

        factory = new JPAQueryFactory(em);
        QMember member = QMember.member;


        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(20, 29)).then(3)
                .when(member.age.between(30, 39)).then(2)
                .when(member.age.between(40, 49)).then(1)
                .otherwise(4);

        List<Tuple> result = factory
                .select(member.username, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc()) // otherwise-> (20~29) -> (30~39) -> (40~49) 순으로 출력
                .fetch();

        for (Tuple s : result) {
            System.out.println("s = " + s);
        }
//        s = [MemberD, 55, 4]
//        s = [MemberA, 25, 3]
//        s = [MemberB, 35, 2]
//        s = [MemberC, 45, 1]
//        s = [MemberE, 49, 1]
    }

}
