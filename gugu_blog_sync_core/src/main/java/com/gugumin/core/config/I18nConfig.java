package com.gugumin.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

/**
 * The type Common config.
 *
 * @Author xiaoaiying
 * @Date 2023 -03-18 15:21
 */
@Configuration
@Slf4j
public class I18nConfig {
    private static final String MESSAGE_BEAN_NAME_SPLIT = ";";
    @Value(value = "${core-config.i18n.name:i18n.messages}")
    private String messageBeanName;

    @Value(value = "${core-config.i18n.lang:en_US}")
    private String lang;

    /**
     * Gets lang.
     *
     * @return the lang
     */
    public String getLang() {
        return this.lang;
    }

    /**
     * Message source message source.
     *
     * @return the message source
     */
    @Bean
    public MessageSource messageSource() {
        Locale locale = StringUtils.parseLocale(lang);
        LocaleContextHolder.setLocale(locale);
        ResourceBundleMessageSource bundle = new ResourceBundleMessageSource();
        bundle.setDefaultEncoding(StandardCharsets.UTF_8.name());
        bundle.addBasenames("i18n.messages");
        bundle.setDefaultLocale(locale);
        Set<String> names = bundle.getBasenameSet();
        for (String name : messageBeanName.split(MESSAGE_BEAN_NAME_SPLIT)) {
            if (!names.contains(name)) {
                bundle.addBasenames(name);
            }
        }
        return bundle;
    }
}