package com.asset.rest.biz;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author fisher
 * @date 2023-09-21: 10:54
 * process loader json config
 */
@Component
public class SourceLoaderBiz implements BeanPostProcessor {

    /**
     * after process bean
     * @param bean the new bean instance
     * @param beanName the name of the bean
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractSourceLoader) {
            AbstractSourceLoader loader = (AbstractSourceLoader) bean;
            loader.loader();
        }
        return bean;
    }
}
