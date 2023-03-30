package com.gugumin.core.utils;

import org.springframework.util.StringUtils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Java代理设置
 *
 * @author minmin
 * @since 2021 /05/02 12:20
 */
public class SystemProxyUtil {
    private SystemProxyUtil() {
    }

    /**
     * 设置代理端口
     *
     * @param host     host
     * @param port     端口
     * @param version  the version
     * @param username the username
     * @param password the password
     */
    public static void setProxy(String host, String port, String version, String username, String password) {
        System.setProperty("socksProxyHost", host);
        System.setProperty("socksProxyPort", port);
        if (StringUtils.hasText(version)) {
            System.setProperty("socksProxyVersion", version);
        }
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            System.setProperty("java.net.socks.username", username);
            System.setProperty("java.net.socks.password", username);
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
        }
    }

    /**
     * 移除代理
     */
    public static void removeProxy() {
        System.getProperties().remove("socksProxyHost");
        System.getProperties().remove("socksProxyPort");
        System.getProperties().remove("socksProxyVersion");
        System.getProperties().remove("java.net.socks.username");
        System.getProperties().remove("java.net.socks.password");
    }
}
