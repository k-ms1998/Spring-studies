package study.querydsl.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    void createMember() {
        Member memberA = new Member("MemberA", 25);
        Member memberB = new Member("MemberB", 30);
        Member memberC = new Member("MemberC", 30);
        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        Assertions.assertThat(members.size()).isEqualTo(3);
    }

}