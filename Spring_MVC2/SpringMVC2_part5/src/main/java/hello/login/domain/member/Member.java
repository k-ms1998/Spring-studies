package hello.login.domain.member;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Member {
    private Long id;

    @NotEmpty
    private String loginId; //로그인 ID; 사용자 입력

    @NotEmpty
    private String name; //사용자 이름

    @NotEmpty
    private String password; //비밀번호

    public Member(String loginId, String name, String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
    }
}
