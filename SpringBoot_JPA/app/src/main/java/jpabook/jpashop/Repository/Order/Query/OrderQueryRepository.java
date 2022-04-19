package jpabook.jpashop.Repository.Order.Query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;


    public List<OrderQueryCollectionDTO> findOrderQueryDtos1() {
        List<OrderQueryCollectionDTO> result = findOrders();

        result.stream()
                .forEach((o) -> {
                    List<OrderItemQueryCollectionDTO> orderItems= findOrderItems(o); // N번의 쿼리가 처리된다
                    o.setOrderItems(orderItems);
                });
        //총 1+N 만큼 쿼리 처리

        return result;
    }

    //DTO & JOIN FETCH를 동시에 사용하는 것은 불가능 -> 그러므로 그냥 JOIN 사용
    public List<OrderQueryCollectionDTO> findOrderQueryDtos2() {
        List<OrderQueryCollectionDTO> result = findOrders();

        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        String query = "select new jpabook.jpashop.Repository.Order.Query.OrderItemQueryCollectionDTO(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIds";
        List<OrderItemQueryCollectionDTO> orderItems = em.createQuery(query, OrderItemQueryCollectionDTO.class)
                .setParameter("orderIds", orderIds)
                .getResultList(); 
        //쿼리문을 처리해서 DB에서 모든 OrderItem 들을 가져옴; in 을 이용해서 Batch로 가져옴 => 쿼리문을 한번만 처리 (vs. V4에서는 N번)
        
        //각 orderItems의 orderId를 result에서 같은 orderId를 가진 값에 set을 해줘야 하는데, 각 result마다 orderItems를 한번씩 다 훑을 경우에는 걸리는 시간이 O(n^2)
        // 그러므로, 각 orderItems들을 한번 돌면서 orderId를 Key로 갖고, List<OrderItemQueryCollectionDTO> 를 Value로 갖는 Map을 생성
        // Map을 생성 후, 각 result를 한번씩 훑어서, orderId를 가져온 후, orderId로 Map을 참조해서 OrderItem를 set 해줍니다
        // => 걸리는 시간 => O(2n)
        Map<Long, List<OrderItemQueryCollectionDTO>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy((orderItemQueryCollectionDTO -> {
                    return orderItemQueryCollectionDTO.getOrderId();
                }))); // K == orderItemQueryCollectDTO.getOrderId()
        result.stream()
                .forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        //총 1+1 만큼 쿼리 처리
        return result;
    }

    public List<OrderFlatDTO> findOrderQueryDtos3() {
        String query = "select" +
                " new jpabook.jpashop.Repository.Order.Query.OrderFlatDTO(o.id, m.username, o.orderDate, d.address, o.status, i.name, oi.orderPrice, oi.count)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d" +
                " join o.orderItems oi" +
                " join oi.item i";
        return em.createQuery(query, OrderFlatDTO.class)
                .getResultList();
    }

    private List<OrderItemQueryCollectionDTO> findOrderItems(OrderQueryCollectionDTO o) {
        String query = "select new jpabook.jpashop.Repository.Order.Query.OrderItemQueryCollectionDTO(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                    " from OrderItem oi" +
                    " join oi.item i" +
                    " where oi.order.id = :orderId";
        return em.createQuery(query, OrderItemQueryCollectionDTO.class)
                .setParameter("orderId", o.getOrderId())
                .getResultList();
    }

    private List<OrderQueryCollectionDTO> findOrders() {
        String query = "select new jpabook.jpashop.Repository.Order.Query.OrderQueryCollectionDTO(o.id, m.username, o.orderDate, o.status, d.address)" +
                    " from Order o" +
                    " join o.member m" +
                    " join o.delivery d";
        return em.createQuery(query, OrderQueryCollectionDTO.class)
                .getResultList();
    }



}
