package com.gugumin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
public class Config {
    private String workspace;

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
