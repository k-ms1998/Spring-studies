package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;
//
//    @GetMapping("/")
//    public String home() {
//        return "home";
//    }

    //    @GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        if (memberId == null) {
            //로그인이 되어있지 않을때는, home.html 랜더링
            return "home";
        }

        //로그인
        Member loginMember = memberRepository.findById(memberId);
        if (loginMember == null) {
            return "home";
        }

        //로그인 성공
        model.addAttribute("member", loginMember);  //로그인한 사람의 정보를 넘겨줌

        return "loginHome";
    }

    //    @GetMapping("/")
    public String sessionLogin(HttpServletRequest req, Model model) {
        //세션 관리자에 저장된 회원 정보 조회
        Member loginMember = (Member) sessionManager.getSession(req);
        if (loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);

        return "loginHome";
    }

    @GetMapping("/")
    public String httpSessionLogin(HttpServletRequest req, Model model) {
        HttpSession session = req.getSession(false); //false => 세션이 존재 하지 않을때 새롭게 생성 X
        if (session == null) {
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);

        return "loginHome";
    }
}