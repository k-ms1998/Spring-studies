package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AImpl implements InterfaceA {

    @Override
    public String call() {
        log.info("callA");
        return "A";
    }
}
