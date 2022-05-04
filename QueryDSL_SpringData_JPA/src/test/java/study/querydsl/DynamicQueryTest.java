package study.querydsl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class DynamicQueryTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory factory;
    QMember member = QMember.member;

    @BeforeEach
    public void before() {
        factory = new JPAQueryFactory(em);

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
    void booleanBuilder() {

        /**
         * username == "MemberA" && age == 25 인 Member를 찾고 싶을때
         */
        String usernameParam = "MemberA";
        Integer ageParam = 25;

        //SELECT *FROM member WHERE username = "MemberA" AND age = 25;
        List<Member> result = searchMember1(usernameParam, ageParam);
        System.out.println("result.get(0) = " + result.get(0));

        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void booleanBuilder2() {

        /**
         * username == "MemberA" Member를 찾고 싶은데, 주어진 age 값이 null 일때
         */
        String usernameParam = "MemberA";
        Integer ageParam = null;

        // SELECT *FROM member WHERE username = "MemberA";
        // ageParam 값이 null 이므로, searchMember1 에서 BooleanBuilder에 age에 대한 조건은 추가 X -> username에 대한 조건만 WHERE에 추가돼서 쿼리 실행
        List<Member> result = searchMember1(usernameParam, ageParam);
        System.out.println("result.get(0) = " + result.get(0));

        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void WhereParam() {

        String usernameParam = "MemberA";
        Integer ageParam = 25;

        List<Member> result = searchMember2(usernameParam, ageParam);
        System.out.println("result.get(0) = " + result.get(0));

        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam) {

        BooleanBuilder builder = new BooleanBuilder();
        if (usernameParam != null) {
            builder.and(member.username.eq(usernameParam));
        }
        if (ageParam != null && ageParam >= 0) {
            builder.and(member.age.eq(ageParam));
        }

        return factory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return factory
                .selectFrom(member)
                .where(usernameEq(usernameParam), ageEq(ageParam)) //where(null)이면 무시 됨 -> ex: where(null, member.username.eq("MemberA")) 이면 null은 무시되고, username = "MemberA" 조건만 추가됨
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameParam) {
        // NULL이 아니면 WHERE 조건 추가됨; NULL 이면 NULL을 그대로 반환
        return usernameParam != null ? member.username.eq(usernameParam) : null;
    }
    private BooleanExpression ageEq(Integer ageParam) {
        // NULL이 아니면 WHERE 조건 추가됨; NULL 이면 NULL을 그대로 반환
        return ageParam != null ? member.age.eq(ageParam) : null;
    }
}
