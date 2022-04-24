package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    @Test
    void basicCRUDTest() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        //단건 조회 검증
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> members = memberJpaRepository.findAll();
        Assertions.assertThat(members.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        org.junit.jupiter.api.Assertions.assertThrows(NoSuchElementException.class, () -> memberJpaRepository.findById(member1.getId()).get());

    }

    @Test
    void findByNameAndAge() {
        Member memberA = new Member("memberA", 20);
        Member memberB = new Member("memberB", 26);
        Member memberC = new Member("memberB", 30);

        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);
        memberJpaRepository.save(memberC);

        List<Member> findMembers = memberJpaRepository.findByUsernameAndAgeGreaterThan("memberB", 25);
        Assertions.assertThat(findMembers.size()).isEqualTo(2);
    }

    @Test
    void findByUsernameNamedQuery() {
        Member memberA = new Member("memberA", 20);
        Member memberB = new Member("memberB", 26);
        Member memberC = new Member("memberB", 30);

        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);
        memberJpaRepository.save(memberC);


        List<Member> findMembers = memberJpaRepository.findByUsername("memberB");
        Assertions.assertThat(findMembers.size()).isEqualTo(2);
    }

}