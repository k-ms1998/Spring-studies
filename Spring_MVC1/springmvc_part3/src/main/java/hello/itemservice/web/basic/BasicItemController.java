package hello.itemservice.web.basic;

import hello.itemservice.domain.Item;
import hello.itemservice.domain.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/basic/items")
public class BasicItemController {
    private final ItemRepository itemRepository;

    @Autowired
    public BasicItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);

        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

        return "basic/item";
    }

    @GetMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

        return "basic/editForm";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Item item,  Model model) {
        Long id = item.getId();
        itemRepository.update(id, item);
        model.addAttribute("item", item);

        return "redirect:/basic/items/"+id;
    }

    /**
     * Using @RequestParam to get req.body info
     * @param itemName
     * @param price
     * @param quantity
     * @return
     */
//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam Integer price,
                       @RequestParam Integer quantity) {

        Item item = new Item(itemName, price, quantity);
        itemRepository.save(item);

        return "basic/addForm";
    }

    /**
     * Using @ModelAttribute to get req.body info
     * @param item
     * @return
     */
    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {
        // @ModelAttribute Item item => O
        // Item item =>  O

        itemRepository.save(item);

        return "basic/addForm";
    }

    @GetMapping("/add")
    public String addForm() {

        return "basic/addForm";
    }

    /**
     * Test Data
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
        itemRepository.save(new Item("itemC", 30000, 30));

    }
}
