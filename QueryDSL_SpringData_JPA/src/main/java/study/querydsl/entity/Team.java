package study.querydsl.entity;

import lombok.Getter;
import lombok.ToString;
import study.querydsl.entity.baseEntity.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(of = {"id", "name"})
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    protected Team() {
    }

    public Team(String name) {
        this.name = name;
    }
}
