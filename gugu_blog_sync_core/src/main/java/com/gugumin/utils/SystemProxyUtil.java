package com.gugumin.utils;

/**
 * Java程序Http代理设置
 *
 * @author minmin
 * @since 2021 /05/02 12:20
 */
public class SystemProxyUtil {
    private SystemProxyUtil() {
    }

    /**
     * 设置本地代理Http端口 无验证
     *
     * @param port 端口
     */
    public static void setHttpProxy(int port) {
        setHttpProxy(String.valueOf(port));
    }

    /**
     * 设置本地代理Http端口 无验证
     *
     * @param port 端口
     */
    public static void setHttpProxy(String port) {
        System.setProperty("proxyHost", "127.0.0.1");
        System.setProperty("proxyPort", port);
    }

    /**
     * 设置代理Http端口
     *
     * @param host host
     * @param port 端口
     */
    public static void setHttpProxy(String host, int port) {
        setHttpProxy(host, String.valueOf(port));
    }

    /**
     * 设置代理Http端口
     *
     * @param host host
     * @param port 端口
     */
    public static void setHttpProxy(String host, String port) {
        System.setProperty("proxyHost", host);
        System.setProperty("proxyPort", port);
    }

    /**
     * 移除本地代理Http
     */
    public static void removeHttpProxy() {
        System.getProperties().remove("proxyHost");
        System.getProperties().remove("proxyPort");
    }
}
