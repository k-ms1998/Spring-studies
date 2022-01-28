package hello.core.filter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.context.annotation.ComponentScan.*;

public class ComponentFilterAppConfigTest {

    @Test
    void filterScan() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
        MyBeanA beanA = ac.getBean("myBeanA", MyBeanA.class);
        //Since MyBeanA is included, ac.getBean(myBeanA) should not be null

        assertThat(beanA).isNotNull();
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> ac.getBean("myBeanB", MyBeanB.class));
        //Because MyBeanB is excluded and is not a bean, ac.getBean(myBeanB) will return an exception

    }

    @Configuration
    @ComponentScan(
            excludeFilters = @Filter(type = FilterType.ANNOTATION,
                    classes = MyExcludeComponent.class),
            includeFilters = @Filter(type=FilterType.ANNOTATION,
                    classes = MyIncludeComponent.class)
    )

    static class ComponentFilterAppConfig {
    }

}
