package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component //Bean 등록
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        //Item == clazz && Item == subItem
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;
        
        if (!StringUtils.hasText(item.getItemName())) {
            //itemName이 입력이 안됐을때
            errors.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 10000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, "가격을 확인해 주세요.");
        }
        if(item.getQuantity() == null || item.getQuantity() > 10000){
            errors.rejectValue("quantity", "max", new Object[]{10000}, "수량을 확인해 주세요.");
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            if (item.getPrice()*item.getQuantity() < 10000) {
                //(objectName, code, arguments, defaultMessage)
                errors.reject("totalPriceMin",new Object[]{10000, item.getPrice()* item.getQuantity()}, "가격*수량 값을 확인해 주세요.");
                errors.reject(null, null, "Test Global Error."); //Adding a test global error
            }
        }
    }
}
