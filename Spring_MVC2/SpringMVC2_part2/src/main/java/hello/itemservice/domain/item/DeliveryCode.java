package hello.itemservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * FAST: 빠른배송
 * NORMAL: 일반 배송
 * SLOW: 느린 배송
 */
@Data //Contains @Getter && @Setter
@AllArgsConstructor
public class DeliveryCode {
    private String code;    //FAST, NORMAL, SLOW
    private String displayName;//빠른 배송, 일반 배송, 느린 배송
    // code=서버에서 처리할때 이름, displayName=사용자에게 보여질 이름

}
