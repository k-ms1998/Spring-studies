package hello.typeconverter.formatter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MyNumberFormatterTest {

    MyNumberFormatter myNumberFormatter = new MyNumberFormatter();

    @Test

    void parse() throws ParseException {
        Number number = myNumberFormatter.parse("1,000", Locale.KOREA);

        Assertions.assertThat(number).isEqualTo(1000L);
    }

    @Test
    void print() {
        String result = myNumberFormatter.print(1000, Locale.KOREA);

        Assertions.assertThat(result).isEqualTo("1,000");
    }
}