package ir.piana.dev.springvue.core.action;

import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

@Configuration
public class ActionConfig {
    @Bean("springVueResource")
    @Scope("singleton")
    public SpringVueResource getSpringVueResource() {
        SpringVueResource springVueResource = ActionInstaller.getSpringVueResource();
        return springVueResource;
    }

    @Bean
    @DependsOn("springVueResource")
    public BeanDefinitionRegistryPostProcessor getBeanDefinitionRegistryPostProcessor(SpringVueResource springVueResource) {
        return new ActionRegistryPostProcessor(springVueResource);
    }
}
