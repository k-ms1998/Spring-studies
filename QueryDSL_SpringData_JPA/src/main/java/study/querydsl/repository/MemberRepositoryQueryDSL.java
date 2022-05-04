package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryQueryDSL {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryQueryDSL(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory; // Main에서 JPAQueryFactory를 빈 등록 해줬음 -> 바로 의존관계 주입 가능
    }

    public void save(Member member) {

        em.persist(member);
    }

    public Optional<Member> findById(Long id) {
        QMember member = QMember.member;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(member)
                        .where(member.id.eq(id))
                        .fetchFirst()
        );
    }

    public List<Member> findAll() {
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(member)
                .fetch();
    }

    public List<Member> findByUsername(String username) {
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }


}
