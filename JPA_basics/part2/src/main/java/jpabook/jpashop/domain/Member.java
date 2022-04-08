package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_ID")
    private Long id;
    private String name;

    @Embedded
    private Address homeAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "WORK_ZIPCODE")),
    })
    private Address workAddress;

    /**
     * MEMBER_ID를 외래키로 갖고, Column들이 MEMBER_ID, FOOD_NAME으로 갖는 FAVORITE_FOOD이름으로 테이블이 생성됨
     */
    @ElementCollection //DEFAULT fetch = FetchType.LAZY
    @CollectionTable(name = "FAVORITE_FOOD",
            joinColumns = @JoinColumn(name = "MEMBER_ID")
    )
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>();

    /**
     * @ElementCollection보다는 AdressEntity를 생성 후, 연관관계를 맺어서 관리하는 것이 더 적합한 방식
     * -> 즉, @ElementCollection은 그다지 선호되는 방식이 아니므로, 실무에서는 사용 X
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressHistory = new ArrayList<>();

    /**
     * cascade = CascadeType.PERSIST || cascade = CascadeType.ALL
     * => orderList에 연관된 order 엔티티들을 추가 시키면, order 엔티티들을 추가로 persist() 하지 않아도,
     * 연관된 member 엔티티를 persist() 해주면 orderList에 있는 order 엔티티들도 자동으로 persist() 됨
     *
     * 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음 X
     * 단지 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함 을 제공할 뿐
     *
     * Parent랑 Child의 라이프사이클이 동일할 때(ex: 등록, 삭제) && 단일 소유자일때(Child와 연관관계가 성립되어 있는 Parent엔티티가 하나만 있을때)
     */
    @OneToMany(mappedBy = "memberId", cascade = CascadeType.PERSIST) // Order에서 외례키로 설정되어 있는 memberId 변수를 mappedBy로 설정
    private List<Order> orderList = new ArrayList<>(); //new ArrayList<>()로 초기화 시켜주는 것이 JPA에서의 관례

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Address getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(Address workAddress) {
        this.workAddress = workAddress;
    }

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }
}
