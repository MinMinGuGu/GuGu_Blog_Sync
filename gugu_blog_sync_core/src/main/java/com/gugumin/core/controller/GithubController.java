package com.gugumin.core.controller;

import com.gugumin.core.event.listener.AppStartedListener;
import com.gugumin.core.service.IHandlerWebhook;
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
    private final IHandlerWebhook gitHubWebHookImpl;

    /**
     * Instantiates a new Webhook controller.
     *
     * @param gitHubWebHookImpl the githubWebhook
     */
    public GithubController(IHandlerWebhook gitHubWebHookImpl) {
        this.gitHubWebHookImpl = gitHubWebHookImpl;
    }

    /**
     * Webhook response entity.
     *
     * @param payload the payload
     * @return the response entity
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody String payload) {
        if (AppStartedListener.consumeInitFlag()) {
            return ResponseEntity.ok().build();
        }
        gitHubWebHookImpl.handler(payload);
        return ResponseEntity.ok().build();
    }
}
