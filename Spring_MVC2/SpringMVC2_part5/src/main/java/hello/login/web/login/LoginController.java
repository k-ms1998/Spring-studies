package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping
public class LoginController {
    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    //    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult
            , HttpServletResponse res) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호룰 확인해주세요.");

            return "login/loginForm";
        }

        //로그인 성공

        //세션 관리자를 통해 세션을 생성하고, 회원 데이터를 보관
        sessionManager.createSession(loginMember, res);
//        쿠키에 시간 정보를 주지 않으면 세션 쿠키 => 브라우저 종료시 삭제

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginHttpSession(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult
            , HttpServletRequest req, HttpServletResponse res) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호룰 확인해주세요.");

            return "login/loginForm";
        }

        //로그인 성공

        //세션이 있으면 세션 반환, 없으면 새로 생성 후 반환
        HttpSession session = req.getSession();
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }

    //    @PostMapping("/logout")
    public String logout(HttpServletRequest req, HttpServletResponse res) {
//        expireCookie(res, "memberId");
        sessionManager.expire(req);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String httpSessionLogout(HttpServletRequest req, HttpServletResponse res) {
        req.getSession().invalidate();

        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse res, String cookieName) {

        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        res.addCookie(cookie);
    }
}

