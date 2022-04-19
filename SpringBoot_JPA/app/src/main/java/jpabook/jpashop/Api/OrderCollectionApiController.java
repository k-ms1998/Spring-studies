package jpabook.jpashop.Api;

import jpabook.jpashop.Domain.Address;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.OrderItem;
import jpabook.jpashop.Domain.OrderStatus;
import jpabook.jpashop.Repository.Order.Query.OrderFlatDTO;
import jpabook.jpashop.Repository.Order.Query.OrderQueryCollectionDTO;
import jpabook.jpashop.Repository.Order.Query.OrderQueryRepository;
import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Repository.OrderSearch;
import jpabook.jpashop.Service.Query.OrderQueryService;
import jpabook.jpashop.Service.Query.OrderQueryServiceCollectionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderCollectionApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderQueryService orderQueryService;

    /**
     * V1: 엔티티를 그대로 사용
     * OrderSimpleApiController에서의 V1과 똑같은 문제점들 발생
     */
    @GetMapping("api/v1/collection-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            //영속성 컨텍스트에 추가해주기
            order.getId();
            order.getMember().getUsername(); //Lazy 강제 초기화
            order.getOrderDate();
            order.getDelivery().getAddress(); //Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream()
                    .forEach(o -> o.getItem().getName()); //Lazy 강제 초기화
        }

        return all;
    }

    @GetMapping("api/v2/collection-orders")
    private Result ordersV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderCollectionDTO> orderDTOS = orders.stream()
                .map(o -> new OrderCollectionDTO(o))
                .collect(Collectors.toList());

        return new Result(orderDTOS);
        /**
         * 지연 로딩 때문에 최악의 경우 총 1+5N의 쿼리문이 처리됩니다:
         * member , address 2N번(order 조회 수 만큼)
         * orderItem N번(order 조회 수 만큼)
         * item 2N번(orderItem 조회 수 만큼)(order.getItem().getName())
         */
    }

    @GetMapping("api/v3_0/collection-orders")
    private Result ordersV3_0() {
        List<Order> orders = orderRepository.findAllWithItem0();
        List<OrderCollectionDTO> orderDTOS = orders.stream()
                .map(o -> new OrderCollectionDTO(o))
                .collect(Collectors.toList());

        return new Result(orderDTOS);
        /**
         * JOIN FETCH로 ORDER과 OrderItems도 join 됩니다.
         * 이떄, SQL 문으로 생각해 보면
         * SELECT  *FROM orders o JOIN order_item oi ON o.order_id = oi.order_id; 과 유사합니다
         * 이때, 총 반환되는 튜플은 4개 입니다(Order에 2개의 튜플, OrderItem에 4개의 튜플이 있고, Order 한개당 OrderItem 2개와 연관관계가 성립된다고 가정).
         * 그렇기 때문에, ORDER가 실제로는 2개인데, JOIN FETCH로 하게되면 ORDER가 4개인것처럼 되며, 중복이 생깁니다.
         * {
         *     "data": [
         *         {
         *             "orderId": 4,
         *             "username": "userA",
         *             "orderDate": "2022-04-19T11:14:55.235249",
         *             "orderStatus": "ORDER",
         *             "address": {
         *                 (생략)
         *             },
         *             "orderItems": [
         *                 {
         *                     "itemName": "JPA1 Book",
         *                     (생략)
         *                 },
         *                 {
         *                     "itemName": "JPA2 Book",
         *                     (생략)
         *                 }
         *             ]
         *         },
         *         {
         *             "orderId": 4,
         *             "username": "userA",
         *             "orderDate": "2022-04-19T11:14:55.235249",
         *             "orderStatus": "ORDER",
         *             "address": {
         *                 (생략)
         *             },
         *             "orderItems": [
         *                 {
         *                     "itemName": "JPA1 Book",
         *                     (생략)
         *                 },
         *                 {
         *                     "itemName": "JPA2 Book",
         *                     (생략)
         *                 }
         *             ]
         *         },
         *         {
         *             "orderId": 11,
         *             "username": "userB",
         *             "orderDate": "2022-04-19T11:14:55.356448",
         *             "orderStatus": "ORDER",
         *             "address": {
         *                 (생략)
         *             },
         *             "orderItems": [
         *                 {
         *                     "itemName": "Spring1 Book",
         *                     (생략)
         *                 },
         *                 {
         *                     "itemName": "Spring2 Book",
         *                     (생략)
         *                 }
         *             ]
         *         },
         *         {
         *             "orderId": 11,
         *             "username": "userB",
         *             "orderDate": "2022-04-19T11:14:55.356448",
         *             "orderStatus": "ORDER",
         *             "address": {
         *                 (생략)
         *             },
         *             "orderItems": [
         *                 {
         *                     "itemName": "Spring1 Book",
         *                     (생략)
         *                 },
         *                 {
         *                     "itemName": "Spring2 Book",
         *                     (생략)
         *                 }
         *             ]
         *         }
         *     ]
         * }
         */
    }

    /**
     * Respository에서 createQuery를 할때 distinct를 추가해서 V3_0의 문제점 해결
     * But,여전히 컬렉션(OrderItems)을 패치 조인시 페이징이 불가능한 오류는 존재합니다
     * V3_2에서 해결
     */
    @GetMapping("api/v3_1/collection-orders")
    private Result ordersV3_1() {
        List<Order> orders = orderRepository.findAllWithItem1();
        List<OrderCollectionDTO> orderDTOS = orders.stream()
                .map(o -> new OrderCollectionDTO(o))
                .collect(Collectors.toList());

        return new Result(orderDTOS);
        /**
         * (쿼리문:)
         * select distinct order0_.order_id as order_id1_9_0_, member1_.member_id as member_i1_6_1_, delivery2_.delivery_id as delivery1_4_2_, orderitems3_.order_item_id as order_it1_8_3_, item4_.item_id as item_id2_5_4_, order0_.member_id as member_i4_9_0_, order0_.order_date as order_da2_9_0_, order0_.status as status3_9_0_, member1_.city as city2_6_1_, member1_.street as street3_6_1_, member1_.zipcode as zipcode4_6_1_, member1_.username as username5_6_1_, delivery2_.city as city2_4_2_, delivery2_.street as street3_4_2_, delivery2_.zipcode as zipcode4_4_2_, delivery2_.order_id as order_id6_4_2_, delivery2_.status as status5_4_2_, orderitems3_.count as count2_8_3_, orderitems3_.item_id as item_id4_8_3_, orderitems3_.order_id as order_id5_8_3_, orderitems3_.order_price as order_pr3_8_3_, orderitems3_.order_id as order_id5_8_0__, orderitems3_.order_item_id as order_it1_8_0__, item4_.name as name3_5_4_, item4_.price as price4_5_4_, item4_.quantity as quantity5_5_4_, item4_1_.artist as artist1_0_4_, item4_1_.etc as etc2_0_4_, item4_2_.author as author1_1_4_, item4_2_.isbn as isbn2_1_4_, item4_3_.actor as actor1_7_4_, item4_3_.director as director2_7_4_, item4_.dtype as dtype1_5_4_ from orders order0_ inner join member member1_ on order0_.member_id=member1_.member_id
         * inner join delivery delivery2_ on order0_.order_id=delivery2_.order_id
         * inner join order_item orderitems3_ on order0_.order_id=orderitems3_.order_id inner join item item4_ on orderitems3_.item_id=item4_.item_id
         * left outer join album item4_1_ on item4_.item_id=item4_1_.item_id
         * left outer join book item4_2_ on item4_.item_id=item4_2_.item_id
         * left outer join movie item4_3_ on item4_.item_id=item4_3_.item_id
         * => 쿼리문 총 1개 처리
         * 
         * {
         *     "data": [
         *         {
         *             "orderId": 4,
         *             "username": "userA",
         *             "orderDate": "2022-04-19T11:39:58.086423",
         *             "orderStatus": "ORDER",
         *             "address": {
         *                 "city": "seoul",
         *                 "street": "street",
         *                 "zipcode": "12345"
         *             },
         *             "orderItems": [
         *                 {
         *                     "itemName": "JPA1 Book",
         *                     "orderPrice": 20000,
         *                     "count": 20
         *                 },
         *                 {
         *                     "itemName": "JPA2 Book",
         *                     "orderPrice": 40000,
         *                     "count": 40
         *                 }
         *             ]
         *         },
         *         {
         *             "orderId": 11,
         *             "username": "userB",
         *             "orderDate": "2022-04-19T11:39:58.193136",
         *             "orderStatus": "ORDER",
         *             "address": {
         *                 "city": "ulsan",
         *                 "street": "blvd",
         *                 "zipcode": "22345"
         *             },
         *             "orderItems": [
         *                 {
         *                     "itemName": "Spring1 Book",
         *                     "orderPrice": 10000,
         *                     "count": 1
         *                 },
         *                 {
         *                     "itemName": "Spring2 Book",
         *                     "orderPrice": 20000,
         *                     "count": 2
         *                 }
         *             ]
         *         }
         *     ]
         * }
         */
    }

    /**
     * ManyToOne, OneToOne 관계는 JOIN, JOIN FETCH를 해도 row수를 증가 시키지 않기 때문에 페이징 가능
     * -> 그러므로, 먼저 ManyToOne, OneToOne 관계를 JOIN FETCH로 가져온다 && paging (findALlWithOrderMemberPaging(offset, limit))
     */
    @GetMapping("api/v3_2/collection-orders")
    private Result ordersV3_2(@RequestParam(value = "offset", defaultValue = "0") int offset,
                              @RequestParam(value = "limit", defaultValue = "100") int limit) {
        // /api/v3_2/collection-orders?offset=1&limit=1
        List<Order> orders = orderRepository.findALlWithOrderMemberPaging(offset, limit); //ManyToOne, OneToOne 관계 모두 JOIN FETCH && Paging
        List<OrderCollectionDTO> orderDTOS = orders.stream()
                .map(o -> new OrderCollectionDTO(o))
                .collect(Collectors.toList());

        /**
         * Member와 Delivery에서 JOIN FETCH를 했기 때문에 실행되는 쿼리문은 V2보다 줄었습니다.
         * 하지만, 여기서 목적은 Paging도 가능하게 하는 것이기 때문에 OrderItem과 Item을 가져오올때는 JOIN FETCH를 하지 못했습니다.
         * 대신, 성능 최적화를 하기 위해서 batch_fetch_size/@BatchSize 를 사용합니다
         * 이럴 경우, 실제 처리되는 쿼리문은 V3_1보다는 많지만, DB를 접근하는 횟수는 1번에 끝낼 수 있기 때문에 성능 최적화가 됩니다.
         * 해당 예저를 보면, DB 접근 횟수는 1+1이 됩니다 (vs. V3_0: 1+5N번 && V3_1: 1번)
         * 적용 방법:
         * 1. applicatin.yml에서
         *  spring:
         *   jpa:
         *   properties:
         *   hibernate:
         *   default_batch_fetch_size: 100
         * 2. @BatchSize(size = 100)를 원하는 컬렉션 필드 또는 엔티티 클래스에 적용
         */

        return new Result(orderDTOS);
        /**
         * 1. select order0_.order_id as order_id1_9_0_, member1_.member_id as member_i1_6_1_, delivery2_.delivery_id as delivery1_4_2_, order0_.member_id as member_i4_9_0_, order0_.order_date as order_da2_9_0_, order0_.status as status3_9_0_, member1_.city as city2_6_1_, member1_.street as street3_6_1_, member1_.zipcode as zipcode4_6_1_, member1_.username as username5_6_1_, delivery2_.city as city2_4_2_, delivery2_.street as street3_4_2_, delivery2_.zipcode as zipcode4_4_2_, delivery2_.order_id as order_id6_4_2_, delivery2_.status as status5_4_2_ from orders order0_
         * inner join member member1_ on order0_.member_id=member1_.member_id
         * inner join delivery delivery2_ on order0_.order_id=delivery2_.order_id limit ?
         *
         * 2. select orderitems0_.order_id as order_id5_8_1_, orderitems0_.order_item_id as order_it1_8_1_, orderitems0_.order_item_id as order_it1_8_0_, orderitems0_.count as count2_8_0_, orderitems0_.item_id as item_id4_8_0_, orderitems0_.order_id as order_id5_8_0_, orderitems0_.order_price as order_pr3_8_0_ from order_item orderitems0_ where orderitems0_.order_id
         * in (?, ?)
         *
         * 3. select item0_.item_id as item_id2_5_0_, item0_.name as name3_5_0_, item0_.price as price4_5_0_, item0_.quantity as quantity5_5_0_, item0_1_.artist as artist1_0_0_, item0_1_.etc as etc2_0_0_, item0_2_.author as author1_1_0_, item0_2_.isbn as isbn2_1_0_, item0_3_.actor as actor1_7_0_, item0_3_.director as director2_7_0_, item0_.dtype as dtype1_5_0_ from item item0_ left outer join album item0_1_ on item0_.item_id=item0_1_.item_id
         * left outer join book item0_2_ on item0_.item_id=item0_2_.item_id
         * left outer join movie item0_3_ on item0_.item_id=item0_3_.item_id where item0_.item_id
         * in (?, ?, ?, ?)
         *
         * batch_fetch_size를 설정했기 때문에 2번과 3번 쿼리문에서 in()이 처리됩니다
         */
    }

    /**
     * V3_2와 같은 로직인데, OSIV OFF 일때도 실행되도록 개발한 API
     * OSIV ON -> 어디서든 지연 로딩 가능
     * OSIV OFF -> 트랜젝션 안에서만 지연 로딩이 가능합니다 OR JOIN FETCH 사용
     */
    @GetMapping("api/v3_3/collection-orders")
    private Result ordersV3_3(@RequestParam(value = "offset", defaultValue = "0") int offset,
                              @RequestParam(value = "limit", defaultValue = "100") int limit) {
        // /api/v3_2/collection-orders?offset=1&limit=1
        List<OrderQueryServiceCollectionDTO> orderDTOS = orderQueryService.orderV3_2(offset, limit);

        return new Result(orderDTOS);
    }

    @GetMapping("/api/v4/collection-orders")
    public Result ordersV4() {
        List<OrderQueryCollectionDTO> orderQueryDtos = orderQueryRepository.findOrderQueryDtos1();

        return new Result(orderQueryDtos);
    }

    @GetMapping("/api/v5/collection-orders")
    public Result ordersV5() {
        List<OrderQueryCollectionDTO> orderQueryDtos = orderQueryRepository.findOrderQueryDtos2();

        return new Result(orderQueryDtos);
    }

    @GetMapping("/api/v6/collection-orders")
    public Result ordersV6() {
        List<OrderFlatDTO> orderQueryDtos = orderQueryRepository.findOrderQueryDtos3();
        /**
         * orderQueryDtos는 member, delivery, orderItem, item을 모두 JOIN한 쿼리를 반환한 값을 갖고 있습니다
         * 이럴 경우, 앞서 V2에서 봤던 중복된 값을 갖고 있는 오류가 여전히 존해 합니다.
         * 오류를 해결하기 위해서 중복을 제거하는 로직을 실행 합니다
         */


        return new Result(orderQueryDtos);
    }


    @Data
    static class OrderCollectionDTO {
        private Long orderId;
        private String username; //주문자 이름
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //배송지
        private List<OrderItemDTO> orderItems;

        /**
         * Response를 반환 할때 엔티티 반환 X
         * 그 뜻은, DTO에서도 엔티티 반환 X
         * 그러므로, OrderItem도 DTO로 변환해서 반환
         * 또한, OrderItem도 DTO로 변환하면 클라이언트가 원하는 값만 보낼 수 있음
         */

        public OrderCollectionDTO(Order order) {
            this.orderId = order.getId();
            this.username = order.getMember().getUsername();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItems = order.getOrderItems().stream()
                    .map((o) -> new OrderItemDTO(o))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDTO {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDTO(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        public T data;
    }

}
