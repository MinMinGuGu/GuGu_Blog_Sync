package com.gugumin.service.impl;

import com.gugumin.components.SiteObserver;
import com.gugumin.config.Config;
import com.gugumin.pojo.Article;
import com.gugumin.service.IGithubWebhook;
import com.gugumin.utils.FileUtil;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

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

    /**
     * Instantiates a new Github webhook.
     *
     * @param siteObserver the site observer
     * @param config       the config
     */
    public GithubWebhookImpl(SiteObserver siteObserver, Config config) {
        this.siteObserver = siteObserver;
        this.config = config;
    }

    @Override
    public void handler(String payload) {
        Path repositoryPath = updateRepository();
        JSONArray added = JsonPath.read(payload, "$.head_commit.added");
        siteObserver.postNotice(analyzeAndRead(repositoryPath, added), SiteObserver.NoticeType.ADD_ARTICLE);
        JSONArray removed = JsonPath.read(payload, "$.head_commit.removed");
        siteObserver.postNotice(analyzeAndRead(repositoryPath, removed), SiteObserver.NoticeType.REMOVE_ARTICLE);
        JSONArray modified = JsonPath.read(payload, "$.head_commit.modified");
        siteObserver.postNotice(analyzeAndRead(repositoryPath, modified), SiteObserver.NoticeType.UPDATE_ARTICLE);
    }

    private Path updateRepository() {
        Path repositoryPath = config.getRepositoryPath();
        try (Git git = Git.open(repositoryPath.toFile())) {
            PullCommand pullCommand = git.pull()
                    .setRemote("origin");
            pullCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getGit().getUsername(), config.getGit().getToken()));
            pullCommand.call();
            log.info("更新本地分支成功");
        } catch (Exception e) {
            log.error("更新git仓库失败");
            throw new RuntimeException(e);
        }
        return repositoryPath;
    }

    private List<Article> analyzeAndRead(Path repositoryPath, JSONArray jsonArray) {
        if (jsonArray.size() < 1) {
            return Collections.emptyList();
        }
        return jsonArray.stream().map(obj -> {
            String fileUri = obj.toString();
            if (!fileUri.endsWith(MD_SUFFIX)) {
                return null;
            }
            Path filePath = repositoryPath.resolve(fileUri);
            String context = FileUtil.read(filePath);
            String title = fileUri.substring(fileUri.lastIndexOf("/") + 1).replace(MD_SUFFIX, "");
            return new Article(title, context);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
