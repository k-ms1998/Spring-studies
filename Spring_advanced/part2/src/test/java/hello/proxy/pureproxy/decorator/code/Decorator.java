package hello.proxy.pureproxy.decorator.code;

public abstract class Decorator {

    private Component component;

    public Decorator(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }
}
