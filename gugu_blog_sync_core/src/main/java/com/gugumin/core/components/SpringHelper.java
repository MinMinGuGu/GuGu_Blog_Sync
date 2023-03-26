package com.gugumin.core.components;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * The type Spring helper.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Component
public class SpringHelper implements ApplicationContextAware {
    @Getter
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
