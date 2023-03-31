package com.gugumin.core.config;

import com.gugumin.core.components.I18nHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type Web config.
 *
 * @author minmin
 * @date 2023 /03/18
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final I18nHelper i18nHelper;

    /**
     * Instantiates a new Web config.
     *
     * @param i18nHelper the 18 n helper
     */
    public WebConfig(I18nHelper i18nHelper) {
        this.i18nHelper = i18nHelper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GithubInterceptors(i18nHelper)).addPathPatterns(GithubInterceptors.URI_PATTERN);
    }

    private static class GithubInterceptors implements HandlerInterceptor {
        private static final String LOG_HEADER_USER_AGENT = "log_header_user_agent";
        private static final String LOG_HEADER_X_GITHUB_EVENT = "log_header_x_github_event";

        private static final String URI_PATTERN = "/github/webhook";
        private static final String HEAD_USERAGENT = "User-Agent";
        private static final String HEAD_X_GITHUB_EVENT = "X-GitHub-Event";
        private static final String DEFAULT_USERAGENT = "GitHub-Hookshot";
        private static final String DEFAULT_EVENT = "push";
        private final I18nHelper i18nHelper;

        /**
         * Instantiates a new Github interceptors.
         *
         * @param i18nHelper the 18 n helper
         */
        public GithubInterceptors(I18nHelper i18nHelper) {
            this.i18nHelper = i18nHelper;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            if (!request.getHeader(HEAD_USERAGENT).startsWith(DEFAULT_USERAGENT)) {
                log.info(i18nHelper.getI18nMessage(LOG_HEADER_USER_AGENT), DEFAULT_USERAGENT);
                return false;
            }
            if (!DEFAULT_EVENT.equals(request.getHeader(HEAD_X_GITHUB_EVENT))) {
                log.info(i18nHelper.getI18nMessage(LOG_HEADER_X_GITHUB_EVENT));
                return false;
            }
            return true;
        }
    }

}
