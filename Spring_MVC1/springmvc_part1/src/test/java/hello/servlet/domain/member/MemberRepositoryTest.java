package hello.servlet.domain.member;

import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MemberRepositoryTest {
    MemberRepository memberRepository = MemberRepository.getInstance();

    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void save() {
        //given
        Member member = new Member("hello", 20);

        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(savedMember.getId());
        Assertions.assertThat(findMember).isSameAs(savedMember);
    }

    @Test
    void findAll() {
        //given
        List<Member> memberList = new ArrayList<>();
        Member memberA = new Member("helloA", 20);
        Member memberB = new Member("helloB", 21);
        Member memberC = new Member("helloC", 22);
        memberList.add(memberA);
        memberList.add(memberB);
        memberList.add(memberC);

        //when
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);

        //then
        List<Member> savedMembers = memberRepository.findAll();
        Assertions.assertThat(savedMembers).contains(memberA, memberB, memberC);

    }
}
