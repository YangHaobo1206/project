package com.food.delivery.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

/**
 * Aligns mapper factory bean definitions with Spring Boot 3 by converting any
 * {@code factoryBeanObjectType} attributes expressed as Strings into real Class
 * references. Older MyBatis scanners may leave this attribute as a String,
 * which triggers startup failures on Spring 6/Boot 3.
 */
@Configuration
public class FactoryBeanTypeFixConfig implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanNamesForType(Object.class, true, false)) {
            BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
            Object attribute = definition.getAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE);
            if (attribute instanceof String attributeName) {
                try {
                    Class<?> type = ClassUtils.forName(attributeName, beanFactory.getBeanClassLoader());
                    definition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, type);
                } catch (ClassNotFoundException ex) {
                    definition.removeAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE);
                }
            }
        }
    }
}
