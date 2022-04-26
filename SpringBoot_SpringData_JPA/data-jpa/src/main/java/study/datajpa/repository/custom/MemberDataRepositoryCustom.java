package study.datajpa.repository.custom;

import study.datajpa.entity.Member;

import java.util.List;

public interface MemberDataRepositoryCustom {

    List<Member> findMemberCustom();
}
