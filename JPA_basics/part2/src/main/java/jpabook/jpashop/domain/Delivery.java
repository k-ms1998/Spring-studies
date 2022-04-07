package jpabook.jpashop.domain;

import javax.persistence.*;

@Entity
public class Delivery extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "DELIVERY_ID")
    private Long id;

    /**
     * @ManyToOne && @OneToOne DEFAULT는 FetchType.EAGER
     * 그러므로, FetchType.LAZY로 설정해주는게 권장됨
     */
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    private String city;
    private String street;
    private String zipcode;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;


}
