package com.gugumin.core.service.impl;

import com.gugumin.core.config.CoreConfig;
import com.gugumin.core.event.AddArticleEvent;
import com.gugumin.core.event.DeleteArticleEvent;
import com.gugumin.core.event.UpdateArticleEvent;
import com.gugumin.core.pojo.Article;
import com.gugumin.core.pojo.MetaType;
import com.gugumin.core.service.IChangeFileNames;
import com.gugumin.core.service.IGitService;
import com.gugumin.core.service.IHandlerWebhook;
import com.gugumin.core.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * The type Base web hook.
 *
 * @author minmin
 * @date 2023 /03/31
 */
@Slf4j
public abstract class BaseWebHookImpl implements IHandlerWebhook, IChangeFileNames {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(r -> new Thread(r, "WebHook-Task"));
    private static final String MD_SUFFIX = ".md";
    private final CoreConfig coreConfig;
    private final IGitService gitService;
    private final ApplicationEventPublisher publisher;

    /**
     * Instantiates a new Base web hook.
     *
     * @param coreConfig the core config
     * @param gitService the git service
     * @param publisher  the publisher
     */
    protected BaseWebHookImpl(CoreConfig coreConfig, IGitService gitService, ApplicationEventPublisher publisher) {
        this.coreConfig = coreConfig;
        this.gitService = gitService;
        this.publisher = publisher;
    }

    @Override
    public void handler(String payload) {
        log.debug("payload: {}", payload);
        Path repositoryPath = coreConfig.getRepositoryPath();
        List<String> deleteFileNames = getDeleteFileNames(payload);
        if (!CollectionUtils.isEmpty(deleteFileNames)) {
            List<Article> articleList = analyzeAndRead(repositoryPath, deleteFileNames);
            EXECUTOR_SERVICE.execute(() -> publisher.publishEvent(new DeleteArticleEvent(this, articleList)));
        }
        gitService.updateRepository();
        List<String> addFileNames = getAddFileNames(payload);
        if (!CollectionUtils.isEmpty(addFileNames)) {
            EXECUTOR_SERVICE.execute(() -> publisher.publishEvent(new AddArticleEvent(this, analyzeAndRead(repositoryPath, addFileNames))));
        }
        List<String> updateFileNames = getUpdateFileNames(payload);
        if (!CollectionUtils.isEmpty(updateFileNames)) {
            EXECUTOR_SERVICE.execute(() -> publisher.publishEvent(new UpdateArticleEvent(this, analyzeAndRead(repositoryPath, updateFileNames))));
        }
    }

    @PreDestroy
    private void closer() {
        EXECUTOR_SERVICE.shutdown();
    }

    private List<Article> analyzeAndRead(Path repositoryPath, List<String> fileNameList) {
        List<Path> pathList = fileNameList.stream().map(repositoryPath::resolve).collect(Collectors.toList());
        List<Article> articleList = new LinkedList<>();
        for (Path path : pathList) {
            String fileName = path.getFileName().toString();
            if (fileName.endsWith(MD_SUFFIX)) {
                String title = fileName.substring(0, fileName.lastIndexOf(MD_SUFFIX));
                String context = FileUtil.read(path);
                MetaType metaType = Article.parseMetaFromContext(context);
                articleList.add(metaType.parseMetaAndConvert(title, context));
            }
        }
        return articleList;
    }
}