package jpabook.jpashop.domain.practice;

import javax.persistence.*;

@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //단일 테이블 전략 => p_item 테이블(Item 객체)만 생성
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) //구현 클래스마다 테이블 전략 => p_item 테이블 생성 X & movie, book, album 테이블에 price, name column 추가됨; 실무에서 절대 사용 X
@Inheritance(strategy = InheritanceType.JOINED) //조인 전략
@DiscriminatorColumn //자동으로 DTYPE Column 생성 & 값 적재; InheritanceType.JOINED 일때만 사용하면 됨; 그 외에는 생략 가능
@Table(name = "p_item")
public abstract class Item {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
