package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidationTest {

    @Test
    void beanValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item(" ", 0, 10001);
        //=> 오류/violation 3개 발생: 이름이 공백, 가격이 Out of Range, 수량이 Max 초과

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        for (ConstraintViolation<Item> violation : violations){
            System.out.println("violation = " + violation);
            System.out.println("violation.message = " + violation.getMessage());
        }

        Assertions.assertThat(violations.size()).isEqualTo(3);
    }

}
