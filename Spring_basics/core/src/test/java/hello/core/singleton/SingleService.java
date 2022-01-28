package hello.core.singleton;

public class SingleService {
    private static final SingleService instance = new SingleService();

    public static SingleService getInstance() {
        return instance;
    }

    private SingleService() {

    }
    
    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
