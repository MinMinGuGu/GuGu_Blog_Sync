package com.gugumin.controller;

import com.gugumin.event.InitEvent;
import com.gugumin.service.IGithubWebhook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The type Webhook controller.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Controller
@RequestMapping("/github")
public class GithubController {
    private final IGithubWebhook githubWebhook;

    /**
     * Instantiates a new Webhook controller.
     *
     * @param githubWebhook the github webhook
     */
    public GithubController(IGithubWebhook githubWebhook) {
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
        if (InitEvent.consumeInitFlag()) {
            return ResponseEntity.ok().build();
        }
        githubWebhook.handler(payload);
        return ResponseEntity.ok().build();
    }
}
