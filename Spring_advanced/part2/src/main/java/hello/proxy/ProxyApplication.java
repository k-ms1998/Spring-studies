package hello.proxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.aopAspect.AopConfig;
import hello.proxy.config.autoProxy.AutoProxyConfig;
import hello.proxy.config.dynamicProxy.DynamicProxyBasicConfig;
import hello.proxy.config.dynamicProxy.DynamicProxyFilterConfig;
import hello.proxy.config.postProcessor.BeanPostProcessorConfig;
import hello.proxy.config.proxyFactory.ProxyFactoryConfigV1;
import hello.proxy.config.proxyFactory.ProxyFactoryConfigV2;
import hello.proxy.config.v1_proxy.InterfaceProxyConfig;
import hello.proxy.config.v2_proxy.ConcreteProxyConfig;
import hello.proxy.trace.logtrace.LogTrace;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//@Import({AppV1Config.class, AppV2Config.class}) // AppV1Config 클래스를 스프링 빈으로 등록; 필수
//@Import(InterfaceProxyConfig.class)
//@Import(ConcreteProxyConfig.class)
//@Import(DynamicProxyBasicConfig.class)
//@Import(DynamicProxyFilterConfig.class)
//@Import({ProxyFactoryConfigV1.class, ProxyFactoryConfigV2.class})
//@Import(BeanPostProcessorConfig.class)
//@Import(AutoProxyConfig.class)
@Import(AopConfig.class)
@SpringBootApplication(scanBasePackages = "hello.proxy.app") //주의; hello.proxy.app 하위의 파일들만 ComponentScan
public class ProxyApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProxyApplication.class, args);
	}

	// LogTrace를 빈 등록 해주면 InterfaceProxyCofig랑 ConcreteProxyConfig에서 자동으로 LogTrace가 주입된다
	@Bean
	public LogTrace logTrace() {
		return new ThreadLocalLogTrace();
	}

}
