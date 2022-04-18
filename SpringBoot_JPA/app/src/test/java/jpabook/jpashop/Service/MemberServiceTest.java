package jpabook.jpashop.Service;

import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입() {
        //given
        Member member = new Member();
        member.setUsername("Kim");

        //when
        Long savedId = memberService.join(member);

        //then
        Assertions.assertEquals(member, memberService.findOne(savedId));
    }

    @Test
    void 중복회원() {
        //given
        Member memberA = new Member();
        memberA.setUsername("Kim");
        memberService.join(memberA);

        //when
        Member memberB = new Member();
        memberB.setUsername("Kim");

        //then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            memberService.join(memberB);
        });
    }

}