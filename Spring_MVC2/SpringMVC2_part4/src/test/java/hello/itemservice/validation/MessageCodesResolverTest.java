package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class MessageCodesResolverTest {
    MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodeResolverObject() {
        //resolveMessageCodes(errorCode, objectName)
        String[] messageCodes = messageCodesResolver.resolveMessageCodes("required", "item");
        /**
         * 1.: errorCode + "." + objectName
         * 2.: errorCode
         */
        Assertions.assertThat(messageCodes).containsExactly("required.item", "required");

    }

    @Test
    void messageCodeResolverField() {
        //resolveMessageCodes(errorCode, objectName, field, field Type)
        String[] messageCodes = messageCodesResolver.resolveMessageCodes("required", "item", "itemName", String.class);

        /**
         * 1.: errorCode + "." + objectName + "." + field
         * 2.: errorCode + "." + field
         * 3.: errorCode + "." + field Type
         * 4.: errorCode
         */
        Assertions.assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );

    }
}
