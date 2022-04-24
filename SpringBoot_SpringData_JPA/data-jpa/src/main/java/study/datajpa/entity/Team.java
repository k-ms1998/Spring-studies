package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team") //하나의 Team에는 여러 명의 Member가 속할 수 있기 때문에 OneToMany
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

}
