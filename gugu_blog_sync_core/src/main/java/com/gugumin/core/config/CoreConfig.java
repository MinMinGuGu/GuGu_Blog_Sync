package com.gugumin.core.config;

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
    private String workspace;
    private Proxy proxy;
    private Git git;

    @PostConstruct
    private void init() {
        checkProxyConfig();
        checkGitConfig();
    }

    private void checkGitConfig() {
        if (StringUtils.hasText(git.getToken()) && StringUtils.hasText(git.getUsername()) && StringUtils.hasText(git.getRepository())) {
            return;
        }
        throw new RuntimeException("Git配置是必须的");
    }

    private void checkProxyConfig() {
        if (Boolean.TRUE.equals(proxy.getOpen())) {
            if (StringUtils.hasText(proxy.getHost()) && StringUtils.hasText(proxy.getPort())) {
                return;
            }
            throw new RuntimeException("Proxy配置需要host和port参数");
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
