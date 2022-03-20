package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BImpl implements InterfaceB {

    @Override
    public String call() {
        log.info("callB");
        return "B";
    }
}
