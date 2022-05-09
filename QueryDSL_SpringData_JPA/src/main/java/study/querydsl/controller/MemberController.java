package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDTO;
import study.querydsl.repository.MemberRepositoryQueryDSL;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepositoryQueryDSL repository;

    // /v1/members?teamName=TeamA -> 자동으로 condition 에 Parameter 들이 들어감 -> 스프링에서 자동으로 파라미터 바인딩을 해서 condition에 넣어줌
    @GetMapping("/v1/members")
    public List<MemberTeamDTO> searchMemberV1(MemberSearchCondition condition) {
        return repository.searchByWhere(condition);
    }
}
