package com.gugumin.utils;

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
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port);
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port);
    }

    /**
     * 移除本地代理Http
     */
    public static void removeHttpProxy() {
        System.getProperties().remove("http.proxyHost");
        System.getProperties().remove("http.proxyPort");
        System.getProperties().remove("https.proxyHost");
        System.getProperties().remove("https.proxyPort");
    }
}
