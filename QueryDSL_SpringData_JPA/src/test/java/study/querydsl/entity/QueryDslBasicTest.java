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

@SpringBootTest
@Transactional
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
        Member memberB = new Member("MemberB", 35, teamB);
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
    
}
