package com.gugumin.config;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.jgit.util.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


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

    /**
     * Gets repository path.
     *
     * @return the repository path
     */
    public Path getRepositoryPath() {
        String projectName = git.repository.substring(git.repository.lastIndexOf("/") + 1).replace(".git", "");
        return Paths.get(workspace, projectName);
    }


    @PostConstruct
    public void init() {
        if (Boolean.TRUE.equals(proxy.getOpen())) {
            if (StringUtils.isEmptyOrNull(proxy.getHost()) || StringUtils.isEmptyOrNull(proxy.getPort())) {
                throw new RuntimeException("Proxy配置未填写完全");
            }
        }
        if (StringUtils.isEmptyOrNull(git.getUsername()) || StringUtils.isEmptyOrNull(git.getToken()) || StringUtils.isEmptyOrNull(git.getRepository())) {
            throw new RuntimeException("Git配置未填写完全");
        }
    }


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
