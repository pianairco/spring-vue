package ir.piana.dev.springvue.core.action;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ActionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        Map<String, String> beanMap = ActionInstaller.getInstance().getBeanMap();
        for(String key : beanMap.keySet()) {
            RootBeanDefinition beanDefinition =
                    new RootBeanDefinition(key);
            registry.registerBeanDefinition(beanMap.get(key), beanDefinition);
        }


//        serviceDefinition.setTargetType(MyService.class); //The service interface
//        serviceDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
