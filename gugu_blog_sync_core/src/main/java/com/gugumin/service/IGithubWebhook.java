package com.gugumin.service;

/**
 * The interface Github webhook.
 *
 * @author minmin
 * @date 2023 /03/08
 */
public interface IGithubWebhook {
    /**
     * Handler.
     *
     * @param payload the payload
     */
    void handler(String payload);
}
