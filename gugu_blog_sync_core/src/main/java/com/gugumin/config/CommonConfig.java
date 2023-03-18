package com.gugumin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

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

    @Value(value = "${core-config.i18n.lang:en_US}")
    private String lang;

    @Bean
    public MessageSource messageSource() {
        LocaleContextHolder.setLocale(getConfigLocale());
        ResourceBundleMessageSource bundle = new ResourceBundleMessageSource();
        bundle.setDefaultEncoding(StandardCharsets.UTF_8.name());
        bundle.addBasenames("i18n.messages");
        bundle.setDefaultLocale(getConfigLocale());
        Set<String> names = bundle.getBasenameSet();
        for (String name : messageBeanName.split(";")) {
            if (!names.contains(name)) {
                bundle.addBasenames(name);
            }
        }
        return bundle;
    }

    private Locale getConfigLocale() {
        String[] langs;
        if (this.lang == null || "".equals(this.lang)
                || ((langs = this.lang.split("_")).length != 2)){
            log.error("config [lang] format is err. {},", lang);
            return LocaleContextHolder.getLocale();
        }
        return new Locale(langs[0], langs[1]);
    }

}