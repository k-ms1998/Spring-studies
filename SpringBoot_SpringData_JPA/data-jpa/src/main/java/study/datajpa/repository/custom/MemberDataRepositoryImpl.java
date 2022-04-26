package study.datajpa.repository.custom;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberDataRepositoryImpl implements MemberDataRepositoryCustom {
    //이름을 (JpaRepository를 상속받은 인터페이스의 이름 + Impl) Or (사용자 정의 인터페이스 명 + Impl) 으로 설정하면 자동으로 스프링 데이터 JPA가 자동으로 스프링 빈 등록을 해줌
    //그러므로, 해당 예제에서는 MemberDataRepositoryImpl OR MemberDataRepositoryCustomImpl 둘 중 하나로 클래스 이름으로 지어주면 자동으로 스프링 빈 등록 됨

    private final EntityManager em;

    /**
     * 단순히 JpaRepository 또는 @Query 를 이용해서 작성하기에는 너무 복잡한 쿼리 일때, 또는 동적 쿼리를일때는 개발자가 직접 메서드와 쿼리문을 정의해서 사용해야 된다
     * 하지만, 꼭 사용해야 되는 것은 아니다
     * 그냥 Spring JPA에서 처럼 클래스 하나를 정의해서 해도 된다
     */
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
