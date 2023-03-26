package com.gugumin.core.service.impl;

import com.gugumin.core.config.CoreConfig;
import com.gugumin.core.event.AddArticleEvent;
import com.gugumin.core.event.DeleteArticleEvent;
import com.gugumin.core.event.UpdateArticleEvent;
import com.gugumin.core.pojo.Article;
import com.gugumin.core.pojo.MetaType;
import com.gugumin.core.service.IGitService;
import com.gugumin.core.service.IGithubWebhook;
import com.gugumin.core.utils.FileUtil;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Github webhook.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Slf4j
@Service
public class GithubWebhookImpl implements IGithubWebhook {
    private static final String MD_SUFFIX = ".md";
    private final CoreConfig coreConfig;
    private final IGitService gitService;
    private final ApplicationEventPublisher publisher;

    /**
     * Instantiates a new Github webhook.
     *
     * @param coreConfig the config
     * @param gitService gitService
     * @param publisher  the publisher
     */
    public GithubWebhookImpl(CoreConfig coreConfig, IGitService gitService, ApplicationEventPublisher publisher) {
        this.coreConfig = coreConfig;
        this.gitService = gitService;
        this.publisher = publisher;
    }

    @Override
    public void handler(String payload) {
        log.debug("payload: {}", payload);
        Path repositoryPath = coreConfig.getRepositoryPath();
        JSONArray removed = JsonPath.read(payload, "$.commits..removed");
        publisher.publishEvent(new DeleteArticleEvent(this, analyzeAndRead(repositoryPath, removed)));
        gitService.updateRepository();
        JSONArray added = JsonPath.read(payload, "$.commits..added");
        publisher.publishEvent(new AddArticleEvent(this, analyzeAndRead(repositoryPath, added)));
        JSONArray modified = JsonPath.read(payload, "$.commits..modified");
        publisher.publishEvent(new UpdateArticleEvent(this, analyzeAndRead(repositoryPath, modified)));
    }

    private List<Article> analyzeAndRead(Path repositoryPath, JSONArray jsonArray) {
        if (CollectionUtils.isEmpty(jsonArray)) {
            return Collections.emptyList();
        }
        List<Article> articleList = new LinkedList<>();
        for (Object o : jsonArray) {
            if (o instanceof JSONArray) {
                JSONArray arrayItem = (JSONArray) o;
                analyzeFileName2Article(repositoryPath, arrayItem, articleList);
            }
        }
        return articleList;
    }

    private void analyzeFileName2Article(Path repositoryPath, JSONArray arrayItem, List<Article> articleList) {
        if (CollectionUtils.isEmpty(arrayItem)) {
            return;
        }
        for (Object o1 : arrayItem) {
            String fileUri = o1.toString();
            if (fileUri.endsWith(MD_SUFFIX)) {
                Path filePath = repositoryPath.resolve(fileUri);
                String title = fileUri.substring(fileUri.lastIndexOf("/") + 1).replace(MD_SUFFIX, "");
                String context = FileUtil.read(filePath);
                MetaType metaType = Article.parseMetaFromContext(context);
                articleList.add(metaType.parseMetaAndConvert(title, context));
            }
        }
    }
}
