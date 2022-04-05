package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Category parent;

    /**
     * 다대다(N:M) 관계는 사용 추천 X && 실무에서 사용 X
     * 중간 테이블(category_item)을 이용해서 item과 category 간 다대다 연관관계 성립
     * But, 실전에서는 중간 테이블에 연결할때 필요한 정보 외에 다른 정보들이 추가되는 경우가 많음
     * 그렇기 때문에, 실무에서는 살용적이지 않음
     */
    @ManyToMany
    @JoinTable(name = "CATEGORY_ITEM",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private List<Item> itemList = new ArrayList<>();

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();


}
