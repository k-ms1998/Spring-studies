package jpabook.jpashop.Domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name; //제품 이름
    private int price; //제품 가격
    private int quantity; //제품 수량

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "items") //실무에서는 다대다 관계 사용 X, but, 현재는 공부하는 중이기 때문에 다대다 관계로 설정 후 학습
    private List<Category> categories;

}
