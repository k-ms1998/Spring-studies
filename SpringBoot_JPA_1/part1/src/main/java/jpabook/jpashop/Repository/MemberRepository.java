package jpabook.jpashop.Repository;

import jpabook.jpashop.Domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Repository: JPA를 직접 사용하는 계층, 엔티티 매니저 사용
 */
@Repository
public class MemberRepository {

    @PersistenceContext //EntityManager을 자동으로 주입 시켜주는 애노테이션
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);

        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.username = :userName", Member.class)
                .setParameter("userName", name)
                .getResultList();
    }
}
