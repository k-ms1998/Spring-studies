package hello.advanced.trace.template.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTemplate {

    public void execute() {
        Long startTime = System.currentTimeMillis();

        //비즈니스 로직 실행
        call(); //상속; 변하는 로직 부분은 자식 클래스를 생성해서 변경함으로써 구현

        //비즈니스 로직 종료
        Long endTime = System.currentTimeMillis();

        log.info("resultTime1={}", endTime-startTime);
    }

    protected abstract void call();
}
