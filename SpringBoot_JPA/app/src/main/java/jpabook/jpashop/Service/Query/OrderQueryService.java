package jpabook.jpashop.Service.Query;

import jpabook.jpashop.Api.OrderCollectionApiController;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public List<OrderQueryServiceCollectionDTO> orderV3_2(int offset, int limit) {
        List<Order> orders = orderRepository.findALlWithOrderMemberPaging(offset, limit); //ManyToOne, OneToOne 관계 모두 JOIN FETCH && Paging
        List<OrderQueryServiceCollectionDTO> orderDTOS = orders.stream()
                .map(o -> new OrderQueryServiceCollectionDTO(o))
                .collect(Collectors.toList());

        return orderDTOS;
    }

}
