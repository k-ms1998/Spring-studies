package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

public interface MemberDataRepository extends JpaRepository<Member, Long> {
//    JpaRepository<Entity, Entity PK Type>

}
