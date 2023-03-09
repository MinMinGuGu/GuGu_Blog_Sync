package com.gugumin.controller;

import com.gugumin.service.IGithubWebhook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * The type Webhook controller.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Controller
public class WebhookController {
    private final IGithubWebhook githubWebhook;

    /**
     * Instantiates a new Webhook controller.
     *
     * @param githubWebhook the github webhook
     */
    public WebhookController(IGithubWebhook githubWebhook) {
        this.githubWebhook = githubWebhook;
    }

    /**
     * Webhook response entity.
     *
     * @param payload the payload
     * @return the response entity
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody String payload) {
        githubWebhook.handler(payload);
        return ResponseEntity.ok().build();
    }
}
