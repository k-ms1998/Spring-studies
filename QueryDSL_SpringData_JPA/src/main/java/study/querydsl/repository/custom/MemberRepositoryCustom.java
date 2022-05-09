package study.querydsl.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDTO;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDTO> search(MemberSearchCondition condition);
    Page<MemberTeamDTO> searchPageable(MemberSearchCondition condition, Pageable pageable);

}
