package com.gugumin.core.components;

import com.gugumin.core.config.I18nConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * The type 18 n helper.
 *
 * @author minmin
 * @date 2023 /03/26
 */
@Slf4j
@Component
public class I18nHelper {
    private final MessageSource messageSource;
    private final I18nConfig i18nConfig;

    /**
     * Instantiates a new 18 n helper.
     *
     * @param messageSource the message source
     * @param i18nConfig    the common config
     */
    public I18nHelper(MessageSource messageSource, I18nConfig i18nConfig) {
        this.messageSource = messageSource;
        this.i18nConfig = i18nConfig;
    }

    /**
     * Gets i 18 n message.
     *
     * @param code the code
     * @return the i 18 n message
     */
    public String getI18nMessage(String code) {
        return getI18nMessage(code, new Object[]{});
    }

    /**
     * Gets i 18 n message.
     *
     * @param code   the code
     * @param params the params
     * @return the i 18 n message
     */
    public String getI18nMessage(String code, Object[] params) {
        Locale locale = StringUtils.parseLocale(i18nConfig.getLang());
        if (locale == null) {
            locale = Locale.US;
        }
        try {
            return messageSource.getMessage(code, params, locale);
        } catch (NoSuchMessageException ex) {
            log.warn("NoSuchMessageException:", ex);
        }
        return code;
    }
}
