package jpabook.jpashop.Repository;

import jpabook.jpashop.Domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;


    @Test
    @Transactional //자동으로 DB를 rollback 시켜줌
    @Rollback(false) //애너테이션을 추가하면 rollback 하지 않고 DB에 값을 그대로 남겨둠
    void testMember() {
        //given
        Member member = new Member();
        member.setUsername("MemberA");

        //when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.findOne(savedId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        /**
         * member랑 findMember는 영속성 컨텍스트에 존재
         * 둘다 식별자(id)가 같으므로, 영속성 컨텍스트에서는 같은 엔티티라고 처리
         * 그렇기 떄문에, Member에 equals()를 따로 설정해주지 않아도 Assertions.assertThat(findMember).isEqualTo(member)는 Pass
         * && findMember == member는 true입니다.
         */
        Assertions.assertThat(findMember).isEqualTo(member);
        Assertions.assertThat(findMember == member).isTrue();
    }
}