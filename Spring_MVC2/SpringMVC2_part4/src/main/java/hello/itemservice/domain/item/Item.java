package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Item {

    private Long id;

    @NotBlank(message="공백 X")   //해당 오류가 발생했을때 message="" 으로 오류 메세지 직접 설정 가능
    private String itemName;

    @NotNull(message="가격은 필수")
    @Range(min = 1000, max = 1000000)   //message 설정안하면 자동으로 설정해줌
    private Integer price;

    @NotNull
    @Max(10000) //10000을 초과하면 오류
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
