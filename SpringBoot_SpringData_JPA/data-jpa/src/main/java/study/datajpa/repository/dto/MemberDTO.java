package study.datajpa.repository.dto;

import lombok.Data;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@Data
public class MemberDTO {

    private Long id;
    private String username;
    private int age;
    private String teamName;

    public MemberDTO(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.age = member.getAge();
        this.teamName = member.getTeam().getName();
    }

    public MemberDTO(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        this.teamName = team.getName();
    }

    public MemberDTO(String username, int age, String teamName) {
        this.username = username;
        this.age = age;
        this.teamName = teamName;
    }
}
