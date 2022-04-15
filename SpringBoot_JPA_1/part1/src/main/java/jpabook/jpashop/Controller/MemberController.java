package jpabook.jpashop.Controller;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm"; // templates/members/createMemberForm.html 랜더링
    }

    @PostMapping("/members/new")
    public String saveForm(@Valid MemberForm memberForm, BindingResult result) {
        /**
         * 1. @Valid를 통해 오류가 없는 지 확인 -> ex: MembeForm에서 name을 필수이므로 @NotEmpty 애노테이션 추가하고, name의 값이 empty인지 아닌지 확인
         * 2. If name이 emtpy이면, @Valid에서 오류를 던짐 & BindingResult에 오류가 들어감
         * 3. createMemberForm.html의 fields.hasError('name')에 오류 & message가 대입 됨
         * (더 자세한 내용은 SpringMVC2 Validation 부분 참고)
         */

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        Member member = Member.createMember(memberForm.getName(), address);

        memberService.join(member);

        return "redirect:/"; // templates/members/createMemberForm.html 랜더링
    }

    @GetMapping("/members")
    public String getMembers(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberList";
    }
}
