package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberDataRepositoryTest {

    /**
     * memberRepository.getClass() 출력 시 -> class com.sun.proxy.$ProxyXXX
     * 아무것도 구현하지 않은 인터페이스 임에도 불구하고 save, findById() 등 이 작동하는 이유는
     * 해당 인터페이스를 보고 스프링이 자동으로 프록시로 구현 클래스를 만들어서 관계 주입 시켜줌
     */
    @Autowired
    private MemberDataRepository memberDataRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");

        Member saveMember = memberDataRepository.save(member);
        Member findMember = memberDataRepository.findById(member.getId()).get();

        Assertions.assertThat(saveMember).isEqualTo(findMember);
    }

    @Test
    void basicCRUDTest() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberDataRepository.save(member1);
        memberDataRepository.save(member2);

        Member findMember1 = memberDataRepository.findById(member1.getId()).get();
        Member findMember2 = memberDataRepository.findById(member2.getId()).get();

        //단건 조회 검증
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> members = memberDataRepository.findAll();
        Assertions.assertThat(members.size()).isEqualTo(2);

        //카운트 검증
        long count = memberDataRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제 검증
        memberDataRepository.delete(member1);
        org.junit.jupiter.api.Assertions.assertThrows(NoSuchElementException.class, () -> memberDataRepository.findById(member1.getId()).get());

    }

}