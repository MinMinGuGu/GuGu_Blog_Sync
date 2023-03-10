package com.gugumin.service.impl;

import com.gugumin.components.SiteObserver;
import com.gugumin.config.Config;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final Config config;
    private final IGitService gitService;

    /**
     * Instantiates a new Github webhook.
     *
     * @param siteObserver the site observer
     * @param config       the config
     * @param gitService
     */
    public GithubWebhookImpl(SiteObserver siteObserver, Config config, IGitService gitService) {
        this.siteObserver = siteObserver;
        this.config = config;
        this.gitService = gitService;
    }

    @Override
    public void handler(String payload) {
        Path repositoryPath = config.getRepositoryPath();
        JSONArray added = JsonPath.read(payload, "$.head_commit.added");
        siteObserver.postNotice(analyzeAndRead(repositoryPath, added), SiteObserver.NoticeType.ADD_ARTICLE);
        JSONArray removed = JsonPath.read(payload, "$.head_commit.removed");
        siteObserver.postNotice(analyzeAndRead(repositoryPath, removed), SiteObserver.NoticeType.REMOVE_ARTICLE);
        JSONArray modified = JsonPath.read(payload, "$.head_commit.modified");
        siteObserver.postNotice(analyzeAndRead(repositoryPath, modified), SiteObserver.NoticeType.UPDATE_ARTICLE);
        gitService.updateRepository();
    }

    private List<Article> analyzeAndRead(Path repositoryPath, JSONArray jsonArray) {
        if (CollectionUtils.isEmpty(jsonArray)) {
            return Collections.emptyList();
        }
        return jsonArray.stream().map(obj -> {
            String fileUri = obj.toString();
            if (!fileUri.endsWith(MD_SUFFIX)) {
                return null;
            }
            Path filePath = repositoryPath.resolve(fileUri);
            String title = fileUri.substring(fileUri.lastIndexOf("/") + 1).replace(MD_SUFFIX, "");
            String context = FileUtil.read(filePath);
            MetaType metaType = Article.parseMetaFromContext(context);
            return metaType.parseMetaAndConvert(title, context);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
