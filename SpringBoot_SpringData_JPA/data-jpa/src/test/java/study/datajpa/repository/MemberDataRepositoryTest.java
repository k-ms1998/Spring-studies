package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberDataRepositoryTest {

    @Autowired
    private MemberDataRepository memberDataRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");

        Member saveMember = memberDataRepository.save(member);
        Member findMember = memberDataRepository.findById(member.getId()).get();

        Assertions.assertThat(saveMember).isEqualTo(findMember);
    }

}