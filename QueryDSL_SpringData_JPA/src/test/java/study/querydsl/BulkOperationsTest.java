package study.querydsl;


import com.querydsl.core.BooleanBuilder;
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
public class BulkOperationsTest {

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

    /**
     * 벌크 연산 시, JPQL/JPA 와 동일하게 DB와 영속성 컨텍스트의 데이터 불일치가 발생합니다(더 자세한거는 앞서 학습한 JPA 내용 확인)
     * 해결 방법도 동일하게 벌크 연산 후에는 영속성 컨텍스트를 초기화 시키기
     */

    @Test
    void bulkUpdate() {
        factory
                .update(member)
                .set(member.username, "Non-Member")
                .where(member.age.lt(40))
                .execute();

        em.flush();
        em.clear();

        Assertions.assertThat(
                factory
                        .selectFrom(member)
                        .where(member.username.eq("Non-Member"))
                        .fetch()
                        .size()
        ).isEqualTo(2);
    }

    @Test
    void bulkAdd() {
        long count = factory
                .update(member)
                .set(member.age, member.age.add(-1)) // 모든 Member의 나이를 1 빼기
                .execute();

        em.flush();
        em.clear();

        Assertions.assertThat(count).isEqualTo(4);
    }

    @Test
    void bulkDelete() {
        factory
                .delete(member)
                .where(member.age.lt(40))
                .execute();

        em.flush();
        em.clear();

        Assertions.assertThat(
                factory
                        .selectFrom(member)
                        .fetch()
                        .size()
        ).isEqualTo(2);
    }
}
