package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
public class BeanPostProcessorTest {

    /**
     * 일반적인 스프링 빈 등록; A만 등록
     */
    @Test
    void basic() {

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BasicConfig.class);  //A는 빈으로 등록된다; B는 등록되지 않은 상태

        A a = applicationContext.getBean("beanA", A.class);
        a.helloA();

        //B는 빈으로 등록되지 않음 -> getBean 실행시 예외 발생

    }

    @Test
    void postProcessor() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class);

        /**
         * beanA 이름으로 B 객체가 빈으로 등록된다
         */
        B b = applicationContext.getBean("beanA", B.class);
        b.helloB();

        /**
         * A는 빈으로 등록 X
         * BeanPostProcessorConfig에서 bean A를 bean B로 바꿔치기 했으므로, getBean(A.class)는 예외 발생
         */
        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(A.class));

    }


    @Configuration
    static class BasicConfig {

        @Bean(name = "beanA")
        public A a() {
            return new A();
        }


    }

    @Configuration
    static class BeanPostProcessorConfig {

        @Bean(name = "beanA")
        public A a() {
            return new A();
        }

        /**
         * AtoBPostProcessor 를 빈 등록
         * @return
         */
        @Bean
        public AtoBPostProcessor postProcessorAtoB() {
            return new AtoBPostProcessor();
        }
    }

    static class AtoBPostProcessor implements BeanPostProcessor {
        /**
         * postProcessBeforeInitialization : 객체 생성 이후에 @PostConstruct 같은 초기화가 발생하기 전에 호출되는 포스트 프로세서이다.
         * postProcessAfterInitialization : 객체 생성 이후에 @PostConstruct 같은 초기화가 발생한 다음에 호출되는 포스트 프로세서이다.
         */

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            log.info("beanName={} bean={}", beanName, bean);

            /**
             * beanA 가 넘어오면 B를 반환하도록 설정 => A 를 B로 바꿔치기
             * beanA 가 아니면, 들어온 bean 그래도 다시 return
             */
            if (bean instanceof A) {
                return new B();
            }

            return bean;
        }
    }



    static class A {
        public void helloA() {
            log.info("hello A");
        }
    }

    static class B {
        public void helloB() {
            log.info("hello B");
        }
    }



}
