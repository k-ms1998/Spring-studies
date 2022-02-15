package hello.itemservice.messages;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MessageSourceTest {
    @Autowired // @Autowired 부재시, 기본으로 등록된 MessageSource Bean을 못찾음
    MessageSource messageSource;

    @Test
    void helloMessage() {
        String result = messageSource.getMessage("hello", null, null);
        assertThat(result).isEqualTo("안녕");
    }

    @Test
    void notFoundMessageCode() {
       assertThatThrownBy(() -> messageSource.getMessage("no_code", null, null))
                .isInstanceOf(NoSuchMessageException.class);
    }

    @Test
    void notFoundMessageCodeDefaultMessage() {
        assertThat(messageSource.getMessage("no_code", null, "Default Message", null))
                .isEqualTo("Default Message");
        // code에 해당하는 메시지를 찾지 못하면, defaultMessage 반환
    }

    @Test
    void helloMessageEn() {
        assertThat(messageSource.getMessage("hello", null, null))
                .isEqualTo("안녕");
        assertThat(messageSource.getMessage("hello", null, Locale.KOREA))
                .isEqualTo("안녕"); //Locale.KOREA(messages_ko.properties) 파일 존재 X => default인 messages.properties 참조
        assertThat(messageSource.getMessage("hello", null, Locale.ENGLISH))
                .isEqualTo("hello"); //Locale.ENGLISH(messages_en.properties) 참조
    }

    @Test
    void helloMessageArgs() {
        String result = messageSource.getMessage("hello.name", new Object[]{"Data1","Data2","Data3"}, null);
        assertThat(result).isEqualTo("안녕 Data1Data2_Data3");
    }
}
