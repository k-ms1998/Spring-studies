package study.querydsl.dto;

import lombok.Data;
import lombok.ToString;
import study.querydsl.entity.Team;

@Data
@ToString(of = {"id", "username", "age"})
public class MemberDTO {

    private Long id;
    private String username;
    private int age;
    private Team team;

    public MemberDTO() {

    }

    public MemberDTO(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
