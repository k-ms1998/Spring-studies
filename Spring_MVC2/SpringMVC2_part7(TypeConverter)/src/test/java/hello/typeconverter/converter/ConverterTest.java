package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConverterTest {

    @Test
    void StringToInteger() {
        StringToIntegerConverter converter = new StringToIntegerConverter();
        Integer result = converter.convert("10");

        Assertions.assertThat(result).isEqualTo(10);
    }

    @Test
    void IntegerToString() {
        IntegerToStringConverter converter = new IntegerToStringConverter();
        String result = converter.convert(10);

        Assertions.assertThat(result).isEqualTo("10");
    }

    @Test
    void StringToIpPort() {
        StringToIpPortConverter converter = new StringToIpPortConverter();

        IpPort result = converter.convert("127.0.0.1:8080");

        Assertions.assertThat(result).isEqualTo(new IpPort("127.0.0.1", 8080));
        //@EqualsAndHashCode 때문에 다른 IpPort 객체여도, ip랑 port 값이 서로 같으면 같은 객체라고 처리
    }

    @Test
    void IpPortToString() {
        IpPortToStringConverter converter = new IpPortToStringConverter();

        String result = converter.convert(new IpPort("127.0.0.1", 8080));

        Assertions.assertThat(result).isEqualTo("127.0.0.1:8080");
    }
}
