package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validation V2
 *
 */
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes, Model model) {
        //BindingResult bindingResult는 꼭 @ModelAttribute Item item 직후에 와야된다
        
        //검증 로직
         /**objectName : @ModelAttribute 이름
            *field : 오류가 발생한 필드 이름
            *defaultMessage : 오류 기본 메시지
          */
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1000원이상 100만원 이하만 허용됩니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 10000){
            bindingResult.addError(new FieldError("item", "quantity", "수량은 10000개 이하만 허용됩니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            if (item.getPrice()*item.getQuantity() < 10000) {
                bindingResult.addError(new ObjectError("item", "가격*수량은 10000이상만 허용됩니다. 현재 값="+(item.getPrice()*item.getQuantity())));
                bindingResult.addError(new ObjectError("item", "Global Error Test")); //Adding a test global error
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            //BindingResult는 자동으로 뷰에 넘어감 => model.addAttribute 필요 X
            model.addAttribute("item", item); //생략 가능; 인자로 @ModelAttribute Item item을 받기 때문에 자동으로 model.addAttribute를 해주기 때문
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes, Model model) {
        //BindingResult bindingResult는 꼭 @ModelAttribute Item item 직후에 와야된다

        //검증 로직
        /**
         * objectName : 오류가 발생한 객체 이름
         * field : 오류 필드
         * rejectedValue : 사용자가 입력한 값(거절된 값)
         * bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
         * codes : 메시지 코드; String[]; errors.properties 에서 오류 코드 찾아서 반환; new String[]{"code1", "code2"} -> errors.properties 에서  code1이 없으며느 code2를 찾아서 반환, code2도 없으면 defaultMessage 사용
         * arguments : 메시지에서 사용하는 인자; Object[]; errors.properties 오류 코드에서 인자가 필요할때 값을 넘겨줌
         * defaultMessage : 기본 오류 메시지
         */
        if (!StringUtils.hasText(item.getItemName())) {
            //itemName이 입력이 안됐을때
            bindingResult.addError(
                    new FieldError("item", "itemName", item.getItemName(), false, new String[] {"required.item.itemName", "default.message"}, null, "상품 이름은 필수입니다."));
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {
            bindingResult.addError(
                    new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000},"가격을 확인해 주세요."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 10000){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{10000},"수량을 확인해 주세요."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            if (item.getPrice()*item.getQuantity() < 10000) {
                //(objectName, code, arguments, defaultMessage)
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000,item.getPrice()*item.getQuantity()} ,"가격*수량 값을 확인해 주세요."));
                bindingResult.addError(new ObjectError("item", "Global Error Test")); //Adding a test global error
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            //BindingResult는 자동으로 뷰에 넘어감 => model.addAttribute 필요 X
            model.addAttribute("item", item); //생략 가능; 인자로 @ModelAttribute Item item을 받기 때문에 자동으로 model.addAttribute를 해주기 때문
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes, Model model) {
        //BindingResult bindingResult는 꼭 @ModelAttribute Item item 직후에 와야된다

        //검증 로직
        /** rejectValue(field, errorCode, errorArgs, defaultMessage)
         * field: 오류 필드명
         * errorCode:오류 코드(이 오류 코드는 메시지에 등록된 코드가 아니다. 뒤에서 설명할 messageResolver 를 위한 오류 코드이다.)
         * errorArgs: 오류 코드에 보낼 인자 값(FieldError()에서 arguments 와 동일)
         * defaultMessage
         *
         *  resolveMessageCodes(errorCode, objectName, field, field Type) 호출
         *  => objectName == item
         *
         */
        if (!StringUtils.hasText(item.getItemName())) {
            //itemName이 입력이 안됐을때
            bindingResult.rejectValue("itemName", "required");
        }//==ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult,"itemName", "required");
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, "가격을 확인해 주세요.");
        }
        if(item.getQuantity() == null || item.getQuantity() > 10000){
            bindingResult.rejectValue("quantity", "max", new Object[]{10000}, "수량을 확인해 주세요.");
        }

        /**
         * reject(errorCode, errorArgs, defaultMessage)
         * resolveMessageCodes(errorCode, objectName) 호출
         * => objectName == item
         */
        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            if (item.getPrice()*item.getQuantity() < 10000) {
                //(objectName, code, arguments, defaultMessage)
                bindingResult.reject("totalPriceMin",new Object[]{10000, item.getPrice()* item.getQuantity()}, "가격*수량 값을 확인해 주세요.");
                bindingResult.reject(null, null, "Test Global Error."); //Adding a test global error
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            //BindingResult는 자동으로 뷰에 넘어감 => model.addAttribute 필요 X
            model.addAttribute("item", item); //생략 가능; 인자로 @ModelAttribute Item item을 받기 때문에 자동으로 model.addAttribute를 해주기 때문
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

