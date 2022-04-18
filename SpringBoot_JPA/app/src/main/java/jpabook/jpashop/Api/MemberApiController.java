package jpabook.jpashop.Api;

import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 1편에서는 html form을 이용해서 회원가입을 했고, 2편에서는 API로 회원가입을 하는 것

 * 1편에서는 html을 이용하는 것이고, 2편에는 별도의 프론트 클라이언트가 있는 것
 * 예를 들어서 아이폰, 안드로이드 같은 클라이언트가 있거나 또는 react, vue같은 웹 클라이언트가 있는 것이지요.
 * 그래서 서버는 해당 기능을 API로만 제공한다고 이해하시면 됩니다.
 */

@RestController //@Controller && @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;



    /**
     * V1: Reqeust & Response로 Member 엔티티를 직접 받는다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     * - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
     * - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * 결론
     * - API 요청 파라미터로 절대 엔티티 자체를 받는 것을 기피
     * - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.(CreateMemberRequest)
     */
    @PostMapping("/api/v1/members") //회원 등록 api
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        // @RequestBody == Node.js 에서 req.body 와 같다고 생각하면 됨
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    /**
     * V2: Request & Response로 DTO를 사용
     * Member 엔티티 대신에 CreateMemberRequest 등 을 RequestBody와 매핑한다.
     * 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수 있다.
     * 엔티티와 API 스펙을 명확하게 분리할 수 있다.
     * 엔티티가 변해도 API 스펙이 변하지 않는다.
     * 파라미터로 받는 값들을 더 명확하게 알 수 있다.
     */

    /**
     * 회원 조회
     * 조회 값을 DTO를 사용하지 한고, List<Member>로 엔티티의 리스트를 그대로 반환 할 시, 엔티티의 모든 속성 값들을 반환(id, username, address, orders)
     * 필요하지 않는 정보들도 모두 반환하는 문제가 있으므로, DTO로 반환
     */
    @GetMapping("/api/v2/members")
    public Result getMembersV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> members = findMembers.stream()
                .map((m) -> {
                    return new MemberDTO(m.getId(), m.getUsername());
                }).collect(Collectors.toList());
        System.out.println("membersList = " + members);

        return new Result(members.size(), members);
        /**
         * 결과:
         * {
         *     "count": 2,
         *     "data": [
         *         {
         *             "id": 1,
         *             "username": "usernameB"
         *         },
         *         {
         *             "id": 2,
         *             "username": "updateUsernameB"
         *         }
         *     ]
         * }
         */

        /**
         *  Response에 단순히 members만 배열로 반환하는 경우는 적음; 배열 + 추가적인 값들을 반환할때가 많음
         *  그러므로, Result라는 DTO를 생성 후, 스펙에 맞게 값들을 반환해줌
         */
       
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setUsername(request.getUsername());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    /**
     * 회원 수정
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMember(@RequestBody @Valid UpdateMemberRequest request,
                                             @PathVariable("id") Long id) {

        memberService.update(id, request.getUsername());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(id, findMember.getUsername());
    }

    @Data //Getter, Setter, RequiredArgsConstructor ...
    static class CreateMemberRequest {
        private String username;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String username;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String username;

    }

    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private Long id;
        private String username;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }


}
