package jpabook.jpashop.Controller;

import jpabook.jpashop.Domain.Member;
import jpabook.jpashop.Domain.Order;
import jpabook.jpashop.Domain.item.Item;
import jpabook.jpashop.Repository.OrderSearch;
import jpabook.jpashop.Service.ItemService;
import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createOrder(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "orders/orderForm";
    }

    @PostMapping("/order")
    public String saveOrder(@RequestParam("memberId") Long memberId,
                            @RequestParam("itemId") Long itemId,
                            @RequestParam("count") int count) {

        orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String getOrders(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findAllByCriteria(orderSearch);

        model.addAttribute("orders", orders);

        return "orders/orderList";
    }

    @PostMapping("/order/{id}/cancel")
    public String cancelOrder(@PathVariable Long id) {
        orderService.cancel(id);

        return "redirect:/orders";
    }
}
