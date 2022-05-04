package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryJPA {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryJPA(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory; // Main에서 JPAQueryFactory를 빈 등록 해줬음 -> 바로 의존관계 주입 가능
    }

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public List<Member> findAll() {
        String query = "select m from Member m";
        return em.createQuery(query, Member.class)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        String query = "select m from Member m where m.username = :username";
        return em.createQuery(query, Member.class)
                .setParameter("username", username)
                .getResultList();
    }


}
