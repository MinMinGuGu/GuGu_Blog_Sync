package com.gugumin.config;

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
 * @Author xiaoaiying
 * @Date 2023-03-18 15:21
 */
@Configuration
@Slf4j
public class CommonConfig {
    @Value(value = "${core-config.i18n.name:i18n.messages}")
    private String messageBeanName;

    public static String lang;

    @Value(value = "${core-config.i18n.lang:en_US}")
    public void setLang(String lang) {
        CommonConfig.lang = lang;
    }

    @Bean
    public MessageSource messageSource() {
        Locale locale = StringUtils.parseLocale(lang);
        LocaleContextHolder.setLocale(locale);
        ResourceBundleMessageSource bundle = new ResourceBundleMessageSource();
        bundle.setDefaultEncoding(StandardCharsets.UTF_8.name());
        bundle.addBasenames("i18n.messages");
        bundle.setDefaultLocale(locale);
        Set<String> names = bundle.getBasenameSet();
        for (String name : messageBeanName.split(";")) {
            if (!names.contains(name)) {
                bundle.addBasenames(name);
            }
        }
        return bundle;
    }
}