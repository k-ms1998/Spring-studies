package jpabook.jpashop.Domain.item;

import jpabook.jpashop.Exception.NotEnoughStockException;
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

    /**
     * == 비즈니스 로직 ==
     * 도메인 주도 설계 => 엔티티에서 구현 가능한 비즈니스 로직들은 엔티티에서 바로 구현(ex: 수량 증가/감소) => 객체 지향적
     * ex: quantity는 엔티티에 있으므로, quantity의 값만 변하는 비즈니스 로직은 엔티티에서 바로 구현하는 것이 객체 지향적 & 응집력 있음
     */
    public void increaseStock(int quantity) {
        this.quantity += quantity;
    }

    public void decreaseStock(int quantity) {
        int restStock = this.quantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("Need More Stock"); //남은 갯수가 0보다 작을때, 직접 개발한 exception을 던지도록 설계
        }

        this.quantity = restStock;
    }
}
