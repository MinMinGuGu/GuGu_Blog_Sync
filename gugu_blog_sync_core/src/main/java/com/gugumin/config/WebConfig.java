package com.gugumin.config;

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
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GithubInterceptors()).addPathPatterns(GithubInterceptors.URI_PATTERN);
    }

    private static class GithubInterceptors implements HandlerInterceptor {
        private static final String URI_PATTERN = "/github/webhook";
        private static final String HEAD_USERAGENT = "User-Agent";
        private static final String HEAD_X_GITHUB_EVENT = "X-GitHub-Event";
        private static final String DEFAULT_USERAGENT = "GitHub-Hookshot";
        private static final String DEFAULT_EVENT = "push";

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            if (!request.getHeader(HEAD_USERAGENT).startsWith(DEFAULT_USERAGENT)) {
                log.info("User-Agent不以{}开头，请求不处理", DEFAULT_USERAGENT);
                return false;
            }
            if (!DEFAULT_EVENT.equals(request.getHeader(HEAD_X_GITHUB_EVENT))) {
                log.info("X-GitHub-Event不是push，请求不处理");
                return false;
            }
            return true;
        }
    }

}
