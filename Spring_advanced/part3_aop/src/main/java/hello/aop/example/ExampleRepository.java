package hello.aop.example;

import org.springframework.stereotype.Repository;

@Repository
public class ExampleRepository {

    private static int seq = 0;

    /**
     * 5번에 1번씩 실패하느 요청
     */
    public String save(String itemId) {
        seq++;
        if (seq % 5 == 0) {
            throw new IllegalStateException("예외 발생");
        }

        return "ok";
    }
}
