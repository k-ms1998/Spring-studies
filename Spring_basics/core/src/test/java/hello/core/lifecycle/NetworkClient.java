package hello.core.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class NetworkClient {

    private String url;
    private String msg;

    public NetworkClient(){
        System.out.println("생성자 호출: URL="+url);

    }


    public void setUrl(String url) {
        this.url = url;
    }

    //서비스 시작시 호출
    public void connect() {
        System.out.println("connect:"+url);
    }

    public void call(String message) {
        System.out.println("call:"+url+", msg:"+message);
    }

    //서비스 종료시 호출
    public void disconnect(String closeMsg) {
        System.out.println("close:" + url + ", "+ closeMsg);
    }

//      'implements InitializingBean, DisposableBean'
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        connect();
//        call("초기화 연걸 메시지");
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        disconnect("implements DisposableBean");
//    }
    
    public void initBean() {
        connect();
        call("초기화 연결 메시지");
    }
    public void destroyBean() {
        disconnect("destroyMethod='destroyBean()'");
    }

    @PostConstruct
    public void initBeanAnno() {
        connect();
        call("초기화 연결 메시지: Post Construct");
    }

    @PreDestroy
    public void destroyBeanAnno() {
        disconnect("PreDestroy");
    }
    
}
