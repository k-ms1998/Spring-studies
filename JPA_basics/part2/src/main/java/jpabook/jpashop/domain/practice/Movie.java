package jpabook.jpashop.domain.practice;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@DiscriminatorValue(value = "mov") //DTYPE에 적재될 값을 정할 수 있음
public class Movie extends Item{

    private String director;
    private String actor;

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

}
