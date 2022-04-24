package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void save_find() {
        Member member = new Member("data-jpa");
        Member saveMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(member.getId());

        Assertions.assertThat(saveMember.getId()).isEqualTo(findMember.getId());
    }

}