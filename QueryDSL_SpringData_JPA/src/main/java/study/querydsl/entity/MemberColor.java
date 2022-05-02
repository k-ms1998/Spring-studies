package study.querydsl.entity;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@ToString(of = {"name", "color"})
public class MemberColor {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String color;

    public MemberColor(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
