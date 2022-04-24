package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

public interface MemberDataRepository extends JpaRepository<Member, Long> {
//   JpaRepository<Entity, Entity PK Type>
//  @Repository 애노테이션이 없어도 자동으로 JpaRepository를 상속 받으면 Component 스캔이 됨
}
