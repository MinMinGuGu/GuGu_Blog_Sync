package com.gugumin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * The type Filter config.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Slf4j
@Configuration
public class FilterConfig {
    /**
     * Github auth filter filter registration bean.
     *
     * @return the filter registration bean
     */
    @Bean
    public FilterRegistrationBean<GithubAuthFilter> githubAuthFilter() {
        FilterRegistrationBean<GithubAuthFilter> githubAuthFilterFilterRegistrationBean = new FilterRegistrationBean<>();
        githubAuthFilterFilterRegistrationBean.setFilter(new GithubAuthFilter());
        githubAuthFilterFilterRegistrationBean.addUrlPatterns("/*");
        return githubAuthFilterFilterRegistrationBean;
    }

    /**
     * The type Github auth filter.
     */
    public class GithubAuthFilter implements Filter {
        private static final String HEAD_USERAGENT = "User-Agent";
        private static final String HEAD_X_GITHUB_EVENT = "X-GitHub-Event";
        private static final String DEFAULT_USERAGENT = "GitHub-Hookshot";
        private static final String DEFAULT_EVENT = "push";

        @Override
        public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
            Filter.super.init(filterConfig);
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            if (!request.getHeader(HEAD_USERAGENT).startsWith(DEFAULT_USERAGENT)) {
                log.info("User-Agent不以{}开头，请求不处理", DEFAULT_USERAGENT);
                return;
            }
            if (!DEFAULT_EVENT.equals(request.getHeader(HEAD_X_GITHUB_EVENT))) {
                log.info("X-GitHub-Event不是push，请求不处理");
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {
            Filter.super.destroy();
        }
    }
}
