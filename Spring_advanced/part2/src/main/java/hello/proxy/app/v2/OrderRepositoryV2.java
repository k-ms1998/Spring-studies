package hello.proxy.app.v2;

import hello.proxy.app.v2.OrderRepositoryV2;

public class OrderRepositoryV2{

    public void save(String itemId) {
        //저장 로직
        if (itemId.equals("ex")) {
            throw new IllegalStateException("예외 발상");
        }

        mySleep(1000);
    }

    private void mySleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
