package com.gugumin.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @Author xiaoaiying
 * @Date 2023-03-18 15:22
 */
@Component
public class I18nUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(I18nUtils.class);

    @Autowired
    private MessageSource messageSource;

    public String getI18nMessage(String code) {
        return getI18nMessage(code, new Object[] {});
    }
    public String getI18nMessage(String code, String defaultMsg) {
        return getI18nMessage(code, null, defaultMsg);
    }
    public String getI18nMessage(String code, Object[] params) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(code, params, locale);
        } catch (NoSuchMessageException ex) {
            LOGGER.warn("NoSuchMessageException:", ex);
        }
        return code;
    }

    public String getI18nMessage(String code, Object[] params, String defaultMsg) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, params, defaultMsg, locale);
    }
}