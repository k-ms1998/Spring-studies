package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberDataRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberDataRepository memberRepository;

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("MemberA", 25));
        memberRepository.save(new Member("MemberB", 35));
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

}
