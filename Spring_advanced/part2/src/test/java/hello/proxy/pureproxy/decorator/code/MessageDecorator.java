package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecorator extends Decorator implements Component {

    public MessageDecorator(Component component) {
        super(component);
    }

    @Override
    public String operation() {
        log.info("MessageDecorator 실행");

        Component component = this.getComponent();
        String operation = component.operation();
        String decoratorResult = "*****" + operation + "*****";

        log.info("MessageDecorator {} -> {}", operation, decoratorResult);

        return decoratorResult;
    }
}
