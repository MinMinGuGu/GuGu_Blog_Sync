package com.gugumin.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
@Validated
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



    @Getter
    @Setter
    public static class Proxy {

        @NotNull(message = "这里是open")
        private Boolean open;
        @Pattern(regexp = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b")
        private String host;
        @Range(message = "端口为 {min} 到 {max} 之间", min = 0, max = 65535)
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
        @NotBlank(message = "git username不能为空")
        private String username;
        @NotBlank(message = "git token不能为空")
        private String token;
        @NotBlank(message = "git repository不能为空")
        private String repository;
    }
}
