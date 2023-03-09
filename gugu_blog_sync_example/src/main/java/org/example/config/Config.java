package org.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * @author minmin
 * @date 2023/03/08
 */
@ComponentScan("org.example")
@PropertySource("classpath:example.properties")
public class Config {
}
