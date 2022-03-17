package hello.proxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({AppV1Config.class, AppV2Config.class}) // AppV1Config 클래스를 스프링 빈으로 등록; 필수
@SpringBootApplication(scanBasePackages = "hello.proxy.app") //주의; hello.proxy.app 하위의 파일들만 ComponentScan
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

}
