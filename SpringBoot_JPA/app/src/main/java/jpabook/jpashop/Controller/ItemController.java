package jpabook.jpashop.Controller;

import jpabook.jpashop.Domain.item.Book;
import jpabook.jpashop.Domain.item.Item;
import jpabook.jpashop.Service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/new")
    public String createItem(Model model) {
        model.addAttribute("form", new BookForm());

        return "items/createItemForm";
    }

    @PostMapping("/new")
    public String createItem(BookForm bookForm) {
        Book book = Book.createBook(bookForm.getName(), bookForm.getPrice(),
                bookForm.getStockQuantity(), bookForm.getAuthor(), bookForm.getIsbn());

        itemService.saveItem(book);

        return "redirect:/items";
    }

    @GetMapping("")
    public String getItems(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }

    @GetMapping("/{id}/edit")
    public String updateItemForm(Model model, @PathVariable Long id) {
        Book findBook = (Book) itemService.findOne(id);

        BookForm bookForm = new BookForm();
        bookForm.setId(findBook.getId());
        bookForm.setName(findBook.getName());
        bookForm.setPrice(findBook.getPrice());
        bookForm.setStockQuantity(findBook.getQuantity());
        bookForm.setAuthor(findBook.getAuthor());
        bookForm.setIsbn(findBook.getIsbn());

        model.addAttribute("form", bookForm);

        return "items/updateItemForm";
    }

    @PostMapping("/{id}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form, @PathVariable Long id) {
        Book book = Book.createBook(form.getName(), form.getPrice(),
                form.getStockQuantity(), form.getAuthor(), form.getIsbn());
        book.setId(form.getId());
        itemService.saveItem(book);

        /**
         *  id를 수정할 Book의 id로 set을 하지 않으면, 수정 정보의 값으로 새로운 Book이 생성됨 => itemRepository.save 에서 persist() 호출 => insert into 쿼리문 실행;
         *  But, 이미 존재하는 튜플의 id로 세팅 해주면, 새로운 엔티티 생성이 아닌 이미 존재하는 엔티티의 값을 변경 => itemRepository.save 에서 merge() 호출 => update 쿼리문 실행
         *  변경 감지(Dirty Checking)과 비슷
         *  But,Dirty Checking을 사용하는 것이 더 권장. 
         *  Becuase, dirty checking은 변경하는 값만 세팅해주면 되지만, merge()는 변경하지 않는 값들도 모두 세팅해줘야 합니다. 그렇지 않을 경우, 세팅하지 않는 값은 null로 값이 변함
         *  하지만, 현재 예시에서 item을 변경할때는 모든 속성의 값들이 변경이 될수도 있기 때문에, merge를 사용
         */

        return "redirect:/items";
    }
}
