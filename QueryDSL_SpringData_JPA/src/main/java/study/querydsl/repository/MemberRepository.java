package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl.entity.Member;
import study.querydsl.repository.custom.MemberRepositoryCustom;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    //save, findAll, findById 등 모두 스프링 데이터에서 제공

    List<Member> findByUsername(String username);



}

