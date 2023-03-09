package com.gugumin.halo.config;

import com.dtflys.forest.Forest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

/**
 * @author minmin
 * @date 2023/03/08
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties("halo-config")
@PropertySource("classpath:halo-config.properties")
public class HaloConfig {
    private String url;
    private String username;
    private String password;

    @PostConstruct
    private void init() {
        Forest.config().setVariableValue("url", url);
    }
}
