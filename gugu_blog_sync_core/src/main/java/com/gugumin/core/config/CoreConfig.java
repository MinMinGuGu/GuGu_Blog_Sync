package com.gugumin.core.config;

import com.gugumin.core.components.I18nHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The type Config.
 *
 * @author minmin
 * @date 2023 /03/06
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("core-config")
public class CoreConfig {
    private static final String LOG_GIT_CONFIG_REQUIRED = "log_git_config_required";
    private static final String LOG_PROXY_HOST_PORT_REQUIRED = "log_proxy_host_port_required";
    private final I18nHelper i18nHelper;
    private String workspace;
    private Proxy proxy;
    private Git git;

    public CoreConfig(I18nHelper i18nHelper) {
        this.i18nHelper = i18nHelper;
    }

    @PostConstruct
    private void init() {
        checkProxyConfig();
        checkGitConfig();
    }

    private void checkGitConfig() {
        if (StringUtils.hasText(git.getToken()) && StringUtils.hasText(git.getUsername()) && StringUtils.hasText(git.getRepository())) {
            return;
        }
        throw new RuntimeException(i18nHelper.getI18nMessage(LOG_GIT_CONFIG_REQUIRED));
    }

    private void checkProxyConfig() {
        if (Boolean.TRUE.equals(proxy.getOpen())) {
            if (StringUtils.hasText(proxy.getHost()) && StringUtils.hasText(proxy.getPort())) {
                return;
            }
            throw new RuntimeException(i18nHelper.getI18nMessage(LOG_PROXY_HOST_PORT_REQUIRED));
        }
    }

    /**
     * Gets repository path.
     *
     * @return the repository path
     */
    public Path getRepositoryPath() {
        String projectName = git.repository.substring(git.repository.lastIndexOf("/") + 1).replace(".git", "");
        return Paths.get(workspace, projectName);
    }


    /**
     * The type Proxy.
     */
    @Getter
    @Setter
    public static class Proxy {

        private Boolean open;
        private String host;
        private String port;
        private String version;
        private String username;
        private String password;

    }

    /**
     * The type Git.
     */
    @Getter
    @Setter
    public static class Git {

        private String username;

        private String token;

        private String repository;
    }
}
