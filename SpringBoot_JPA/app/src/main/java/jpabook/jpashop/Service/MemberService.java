package jpabook.jpashop.Service;

import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service: 비즈니스 로직, 트랜잭션 처리
 */
@Service
@Transactional(readOnly = true) //DB에서의 데이터의 변경이 없는 읽기 전용 메서드에 사용, 영속성 컨텍스트를 flush() 하지 않으므로 약간의 성능 향상
public class MemberService {

    private final MemberRepository memberRepository; //생성 이후에 변경할 일 X => final로 설정

    public MemberService(MemberRepository memberRepository) {
        //관계 주입
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */
    @Transactional //DB에서의 데이터를 변경 => 그러므로, readOnly = false (DEFAULT)
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        return memberRepository.save(member);
    }


    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 하나만 존회
     */
    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    private void validateDuplicateMember(Member member) {
        //중복 회원이 존재하면 EXCEPTION
        if (!memberRepository.findByName(member.getUsername()).isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다!");
        }
    }
}
