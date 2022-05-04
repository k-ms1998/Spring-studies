package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryJPATest {

    @Autowired
    private MemberRepositoryJPA repository;

    @Test
    void findAll() {
        Member memberA = new Member("MemberA", 20);
        Member memberB = new Member("MemberB", 30);

        repository.save(memberA);
        repository.save(memberB);

        List<Member> members = repository.findAll();

        Assertions.assertThat(members.size()).isEqualTo(2);
    }

    @Test
    void findByUsername() {
        Member memberA = new Member("MemberA", 20);
        Member memberB = new Member("MemberB", 30);

        repository.save(memberA);
        repository.save(memberB);

        List<Member> members = repository.findByUsername("MemberA");

        Assertions.assertThat(members.size()).isEqualTo(1);
    }

}