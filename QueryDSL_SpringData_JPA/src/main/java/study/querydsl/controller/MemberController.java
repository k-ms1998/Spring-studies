package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDTO;
import study.querydsl.repository.MemberRepository;
import study.querydsl.repository.MemberRepositoryQueryDSL;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepositoryQueryDSL repositoryDepre;
    private final MemberRepository repository;

    // /v1/members?teamName=TeamA -> 자동으로 condition 에 Parameter 들이 들어감 -> 스프링에서 자동으로 파라미터 바인딩을 해서 condition에 넣어줌
    @GetMapping("/v1/members")
    public List<MemberTeamDTO> searchMemberV1(MemberSearchCondition condition) {
        return repositoryDepre.searchByWhere(condition);
    }

    @GetMapping("/v2/members")
    public List<MemberTeamDTO> searchMemberV2(MemberSearchCondition condition) {
        return repository.search(condition);
    }

    // /v3/members?teamName=TeamA&page=1&size=5 -> teamName => condition, page && size => pageable
    @GetMapping("/v3/members")
    public Page<MemberTeamDTO> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
        return repository.searchPageable(condition, pageable);
        /**
         * 결과:
         * {
         *     "content": [
         *         {
         *             "memberId": 13,
         *             "username": "member10",
         *             "age": 10,
         *             "teamId": 1,
         *             "teamName": "TeamA"
         *         },
         *         (생략)
         *         {
         *             "memberId": 21,
         *             "username": "member18",
         *             "age": 18,
         *             "teamId": 1,
         *             "teamName": "TeamA"
         *         }
         *     ],
         *     "pageable": {
         *         "sort": {
         *             "empty": true,
         *             "sorted": false,
         *             "unsorted": true
         *         },
         *         "offset": 5,
         *         "pageNumber": 1,
         *         "pageSize": 5,
         *         "paged": true,
         *         "unpaged": false
         *     },
         *     "last": false,
         *     "totalElements": 50,
         *     "totalPages": 10,
         *     "number": 1,
         *     "size": 5,
         *     "sort": {
         *         "empty": true,
         *         "sorted": false,
         *         "unsorted": true
         *     },
         *     "first": false,
         *     "numberOfElements": 5,
         *     "empty": false
         * }
         * => V2와 비교 했을때 Paging에 대한 값들도 같이 반환됨
         */
    }

}
