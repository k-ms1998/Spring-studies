package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToIpPortConverter implements Converter<String, IpPort> {

    @Override
    public IpPort convert(String source) {
//        source == xxx.x.x.x:xxxx
        log.info("Convert source={}",source);

        String[] ipPort = source.split(":");
        String ip = ipPort[0];
        Integer port = Integer.parseInt(ipPort[1]);

        return new IpPort(ip, port);
    }
}
