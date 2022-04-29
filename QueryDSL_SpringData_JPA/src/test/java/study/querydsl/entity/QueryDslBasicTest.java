package study.querydsl.entity;

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
public class QueryDslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory factory;

    @BeforeEach
    public void before() {
        Team teamA = new Team("TeamA");

        em.persist(teamA);
        
        Member memberA = new Member("MemberA", 25, teamA);
        Member memberB = new Member("MemberB", 35, teamA);
        Member memberC = new Member("MemberC", 45, teamA);
        Member memberD = new Member("MemberD", 55, teamA);

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
}
