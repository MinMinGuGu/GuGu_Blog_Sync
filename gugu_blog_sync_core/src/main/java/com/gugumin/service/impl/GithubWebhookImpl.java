package com.gugumin.service.impl;

import com.gugumin.components.SiteObserver;
import com.gugumin.config.CoreConfig;
import com.gugumin.pojo.Article;
import com.gugumin.pojo.MetaType;
import com.gugumin.service.IGitService;
import com.gugumin.service.IGithubWebhook;
import com.gugumin.utils.FileUtil;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
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
    private final SiteObserver siteObserver;
    private final CoreConfig coreConfig;
    private final IGitService gitService;

    /**
     * Instantiates a new Github webhook.
     *
     * @param siteObserver the site observer
     * @param coreConfig   the config
     * @param gitService   gitService
     */
    public GithubWebhookImpl(SiteObserver siteObserver, CoreConfig coreConfig, IGitService gitService) {
        this.siteObserver = siteObserver;
        this.coreConfig = coreConfig;
        this.gitService = gitService;
    }

    @Override
    public void handler(String payload) {
        log.debug("payload: {}", payload);
        Path repositoryPath = coreConfig.getRepositoryPath();
        JSONArray removed = JsonPath.read(payload, "$.commits..removed");
        siteObserver.postNotice(analyzeAndRead(repositoryPath, removed), SiteObserver.NoticeType.REMOVE_ARTICLE);
        gitService.updateRepository();
        JSONArray added = JsonPath.read(payload, "$.commits..added");
        siteObserver.postNotice(analyzeAndRead(repositoryPath, added), SiteObserver.NoticeType.ADD_ARTICLE);
        JSONArray modified = JsonPath.read(payload, "$.commits..modified");
        siteObserver.postNotice(analyzeAndRead(repositoryPath, modified), SiteObserver.NoticeType.UPDATE_ARTICLE);
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
