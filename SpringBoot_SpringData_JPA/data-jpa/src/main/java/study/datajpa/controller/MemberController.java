package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.MemberDataRepository;
import study.datajpa.repository.TeamDataRepository;
import study.datajpa.repository.dto.MemberDTO;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberDataRepository memberRepository;
    private final TeamDataRepository teamDataRepository;

    @PostConstruct
    public void init() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamDataRepository.save(teamA);
        teamDataRepository.save(teamB);

        for (int i = 0; i < 100; i++) {
            Member member = null;
            if (i % 2 == 0) {
                member = new Member("Member" + i, i % 25 + 20, teamA);
            } else {
                member = new Member("Member" + i, i % 25 + 20, teamB);
            }
            memberRepository.save(member);
        }
    }

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {

        Optional<Member> findMemberOptional = memberRepository.findById(id);
        if (findMemberOptional.isEmpty()) {
            return "Member Not Found";
        }

        return findMemberOptional.get().getUsername();
    }

    @GetMapping("/members/convertor/{id}")
    public String findMemberConvertor(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * /members?page=1 => page = 1 값을 자동으로 pageable에 넣어서 파라미터로 넘겨줌; (DEFAULT size = 20)
     * /members?page=1&size=5 => page = 1, size = 5 값을 자동으로 pageable에 넣어서 파라미터로 넘겨줌
     * /members?page=1&size=5&sort=id,desc => page = 1, size = 5, id를 내림차순으로 정렬하는 값을 자동으로 pageable에 넣어서 파라미터로 넘겨줌
     *
     * @PageableDefault로 페이징할 조건들의 기본값들을 지정해 줄 수 있음; (page, size, sort)
     */
    @GetMapping("/members")
    public Page<MemberDTO> list(@PageableDefault(size = 15) Pageable pageable) {
        Page<Member> findMembers = memberRepository.findControllerBy(pageable);
        return findMembers.map(m -> new MemberDTO(m));
    }

}
