package jpabook.jpashop.Api;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderStatus;
import jpabook.jpashop.Repository.DTO.OrderQueryDTO;
import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Repository.OrderSearch;
import jpabook.jpashop.Service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Part 1: ManyToOne, OneToOne 연관관계 성능 최적화
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 *
 * V1(Worst) -> V2(Better) -> V3/V4 (Best)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /**
     * 모든 Order를 엔티티로 반환 받을 때 (엔티티가 모두 지연 로딩일때)
     * => 절대로 발생하면 안될 오류 발생
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        
        return all;
        /**
         * 무한으로 값들을 반환한다
         * Order -> Member -> Orders -> Order ...  서로 양방향으로 참조를 하기 때문에 무한으로 서로 참조하고 값을 반환
         * 해결 방법:
         * 1. 엔티티들을 즉시 로딩으로 변경(FetchType.EAGER) => But, 절대로 즉시 로딩으로  변경 X; 즉시 로딩시 연관관계에 있는 모든 데이터를 가져오기 때문에 성능 저하
         * 2. Hibernate5Module 등록해서 사용
         * But, DTO를 생성해서, DTO로 변환해서 반환하는 것이 추천되는 방법
         */
    }

    /**
     * V2: DTO
     * V1의 문제점을 해결하기 위해 DTO를 이용해서 값을 요청하고 반환
     * V2에서는 LAZY를 유지하면서 V1의 문제점도 해결하지만, LAZY에 의한 새로운 문제점이 발생함
     * findOrders에서 쿼리문이 실행 되는 것은 당연하지만, Order -> OrderDTO 변환 과정에서
     * order.getMember().getUsername과 order.getDelivery().getAddress()에서도 각각 한번씩 쿼리문이 더 실행 됩니다.
     * 그러므로, 1(findOrders를 가져오는 쿼리) + N(모든 findOrders에 대한 getUsername() 쿼리) + N(모든 findOrders에 대한 getAddress() 쿼리) 만큼 쿼리가 실행됨
     * 최악의 경우 총 1 + 2N
     * 불필요하게 많은 쿼리문이 실행되기 때문에, 패치 조인을 이용해서 최적화 합니다
     */
    @GetMapping("/api/v2/simple-orders")
    public Result ordersV2() {
        List<Order> findOrders = orderRepository.findAllByCriteria(new OrderSearch()); //쿼리문 실행
        List<OrderDTO> orderDTOS = findOrders.stream()
                .map(o -> new OrderDTO(o))
                .collect(Collectors.toList());

        return new Result(orderDTOS);
        /**
         * 결과:
         * (쿼리문:)
         * 1. select order0_.order_id as order_id1_9_, order0_.member_id as member_i4_9_, order0_.order_date as order_da2_9_, order0_.status as status3_9_ from orders order0_ inner join member member1_ on order0_.member_id=member1_.member_id where 1=1 limit ?
         * 2. select delivery0_.delivery_id as delivery1_4_0_, delivery0_.city as city2_4_0_, delivery0_.street as street3_4_0_, delivery0_.zipcode as zipcode4_4_0_, delivery0_.order_id as order_id6_4_0_, delivery0_.status as status5_4_0_ from delivery delivery0_ where delivery0_.order_id=?
         * 3. select delivery0_.delivery_id as delivery1_4_0_, delivery0_.city as city2_4_0_, delivery0_.street as street3_4_0_, delivery0_.zipcode as zipcode4_4_0_, delivery0_.order_id as order_id6_4_0_, delivery0_.status as status5_4_0_ from delivery delivery0_ where delivery0_.order_id=?
         * 4. select member0_.member_id as member_i1_6_0_, member0_.city as city2_6_0_, member0_.street as street3_6_0_, member0_.zipcode as zipcode4_6_0_, member0_.username as username5_6_0_ from member member0_ where member0_.member_id=?
         * 5. select member0_.member_id as member_i1_6_0_, member0_.city as city2_6_0_, member0_.street as street3_6_0_, member0_.zipcode as zipcode4_6_0_, member0_.username as username5_6_0_ from member member0_ where member0_.member_id=?
         * => 총 5번의 쿼리문이 처리됨
         *
         * Response:
         * {
         *     "data": [
         *         {
         *             "orderId": 4,
         *             "username": "userA",
         *             "orderDate": "2022-04-18T15:39:03.823905",
         *             "orderStatus": "ORDER",
         *             "address": {
         *                 "city": "seoul",
         *                 "street": "street",
         *                 "zipcode": "12345"
         *             }
         *         },
         *         {
         *             "orderId": 11,
         *             "username": "userB",
         *             "orderDate": "2022-04-18T15:39:03.906687",
         *             "orderStatus": "ORDER",
         *             "address": {
         *                 "city": "ulsan",
         *                 "street": "blvd",
         *                 "zipcode": "22345"
         *             }
         *         }
         *     ]
         * }
         */
    }


    /**
     * V3: DTO + JOIN FETCH
     * V2와 동일한 DTO를 사용하지만, 쿼리문이 여러번 처리되는 문제점을 해결하기 위해 JOIN FETCH를 이용해서 DB로 부터 값을 받아옵니다
     * V2의 문제점을 해결 했지만, 처음에 DB에서 값을 가져올때 엔티티로 가져온 후, 각 엔티티를 DTO로 변환하는 과정이 있기 때문에 여전히 최적화할 여지가 남아있습니다
     * V4에서는 DB에서 값을 가져올때도 DTO 사용해서 원하는 Column만 가져옴
     * */
    @GetMapping("/api/v3/simple-orders")
    public Result ordersV3() {
        List<Order> findOrders = orderRepository.findALlWithOrderMember();
        List<OrderDTO> orderDTOS = findOrders.stream()
                .map(o -> new OrderDTO(o))
                .collect(Collectors.toList());

        return new Result(orderDTOS);
        /**
         * 결과:
         * (쿼리문:)
         * 1. select order0_.order_id as order_id1_9_0_, member1_.member_id as member_i1_6_1_, delivery2_.delivery_id as delivery1_4_2_, order0_.member_id as member_i4_9_0_, order0_.order_date as order_da2_9_0_, order0_.status as status3_9_0_, member1_.city as city2_6_1_, member1_.street as street3_6_1_, member1_.zipcode as zipcode4_6_1_, member1_.username as username5_6_1_, delivery2_.city as city2_4_2_, delivery2_.street as street3_4_2_, delivery2_.zipcode as zipcode4_4_2_, delivery2_.order_id as order_id6_4_2_, delivery2_.status as status5_4_2_ from orders order0_
         * inner join member member1_ on order0_.member_id=member1_.member_id
         * inner join delivery delivery2_ on order0_.order_id=delivery2_.order_id
         * => 총 1번의 쿼리문이 처리됨
         * =>But, 필요 없는 Column들 까지 가져옴
         *
         * 반환되는 Response는 V2와 동일
         */
    }

    /**
     * V4: DTO +JOIN(DTO) (DB에 접근 할때 DTO 사용시 JOIN FETCH 사용 불가; JOIN 사용 해야 됨)
     * V3과 V4는 어떤 것이 더 좋다고 하기에 우열을 가리기 어려움
     *
     * V3는 여러 곳에서 재사용 가능 vs. V4는 DTO를 사용해서 정확히 필요한 데이터만 가져오기 때문에 성능 최적화 면에서 유리
     * 
     * */
    @GetMapping("/api/v4/simple-orders")
    public Result ordersV4() {
        List<OrderQueryDTO> orderDTOS = orderRepository.findALlWithOrderMemberDTO();

        return new Result(orderDTOS);
        /**
         * 결과:
         * (쿼리문:)
         * 1. select order0_.order_id as col_0_0_, member1_.username as col_1_0_, order0_.order_date as col_2_0_, order0_.status as col_3_0_, delivery2_.city as col_4_0_, delivery2_.street as col_4_1_, delivery2_.zipcode as col_4_2_ from orders order0_
         * inner join member member1_ on order0_.member_id=member1_.member_id
         * inner join delivery delivery2_ on order0_.order_id=delivery2_.order_id
         * => 총 1번의 쿼리문이 처리됨 & V3이랑 비교 해봤을때 가져오는 Column이 다름
         *
         * 반환되는 Response는 V2&V3와 동일
         */
    }



    @Data
    static class OrderDTO {
        private Long orderId;
        private String username; //주문자 이름
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //배송지

        public OrderDTO(Order order) {
            this.orderId = order.getId();
            this.username = order.getMember().getUsername();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        public T data;
    }



}
