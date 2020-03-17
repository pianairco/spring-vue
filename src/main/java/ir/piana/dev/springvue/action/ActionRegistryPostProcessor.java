package ir.piana.dev.springvue.action;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

@Component
public class ActionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {

        RootBeanDefinition beanDefinition =
                new RootBeanDefinition("ir.piana.dev.springvue.action.ActionOne"); //The service implementation
//        serviceDefinition.setTargetType(MyService.class); //The service interface
//        serviceDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
        registry.registerBeanDefinition("one", beanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
