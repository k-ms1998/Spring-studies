package study.querydsl.entity;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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

}
