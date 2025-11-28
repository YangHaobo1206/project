package com.example.fooddelivery.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.FactoryBean;

/**
 * Fixes bean definitions where the {@code factoryBeanObjectType} attribute was written as a
 * {@link String}. Spring Boot 3 expects a {@link Class} or {@code ResolvableType}; otherwise,
 * startup fails with {@code Invalid value type for attribute 'factoryBeanObjectType': java.lang.String}.
 *
 * MyBatis mapper scanning in older generated code can leave this attribute as a String, so we
 * translate it back to a {@link Class} at container boot time.
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
