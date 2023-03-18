package com.gugumin.event;

import com.gugumin.components.SiteObserver;
import com.gugumin.config.CoreConfig;
import com.gugumin.pojo.Article;
import com.gugumin.service.IGitService;
import com.gugumin.utils.FileUtil;
import com.gugumin.utils.I18nUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Init event.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Slf4j
@Component
public class InitEvent implements ApplicationListener<ApplicationStartedEvent> {
    private static final String LOG_GIT_INIT_POST = "log_git_init_post";
    private static final String LOG_GIT_INIT_DONE = "log_git_init_done";
    private static final String LOG_SIT_ARTICLE_PULL = "log_site_article_pull";
    private static final String LOG_SIT_ARTICLE_PULL_WRITE = "log_site_article_pull_write";
    private static final String LOG_SIT_ARTICLE_PULL_DONE = "log_site_article_pull_done";
    private static final String LOG_WATCH_WEBHOOK = "log_watch_webhook";

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(r -> new Thread(r, "InitEvent-Task"));
    private static boolean initFlag = false;
    private final SiteObserver siteObserver;
    private final CoreConfig coreConfig;
    private final IGitService gitService;
    private final I18nUtils i18nUtils;


    /**
     * Instantiates a new Init event.
     *
     * @param siteObserver the site observer
     * @param coreConfig   the config
     * @param gitService   gitService
     * @param i18nUtils    i18nUtils
     */
    public InitEvent(SiteObserver siteObserver, CoreConfig coreConfig, IGitService gitService, I18nUtils i18nUtils) {
        this.siteObserver = siteObserver;
        this.coreConfig = coreConfig;
        this.gitService = gitService;
        this.i18nUtils = i18nUtils;
    }

    @PreDestroy
    private void closer() {
        EXECUTOR_SERVICE.shutdownNow();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Path repositoryPath = coreConfig.getRepositoryPath();
        if (Files.notExists(repositoryPath)) {
            log.info(i18nUtils.getI18nMessage(LOG_GIT_INIT_POST));
            gitService.initRepository();
            log.info(i18nUtils.getI18nMessage(LOG_GIT_INIT_DONE));
            tryPullSiteData2Repository(repositoryPath);
        }
        log.info(i18nUtils.getI18nMessage(LOG_WATCH_WEBHOOK));
    }

    /**
     * Consume init flag boolean.
     *
     * @return the boolean
     */
    public static boolean consumeInitFlag() {
        boolean flag = initFlag;
        if (initFlag) {
            initFlag = false;
        }
        return flag;
    }

    @SneakyThrows
    private void tryPullSiteData2Repository(Path repositoryPath) {
        List<Article> articleList = siteObserver.postNotice(null, SiteObserver.NoticeType.GET_ARTICLE);
        if (CollectionUtils.isEmpty(articleList)) {
            return;
        }
        log.info(i18nUtils.getI18nMessage(LOG_SIT_ARTICLE_PULL));
        CountDownLatch countDownLatch = new CountDownLatch(articleList.size());
        for (Article article : articleList) {
            String fileName = article.getName() + ".md";
            if (Files.notExists(repositoryPath.resolve(fileName))) {
                EXECUTOR_SERVICE.execute(() -> {
                    try {
                        FileUtil.write(repositoryPath.resolve(fileName), article.getMetaType().generateMetaAndContext(article));
                        log.info(i18nUtils.getI18nMessage(LOG_SIT_ARTICLE_PULL_WRITE), repositoryPath.resolve(fileName));
                    } finally {
                        countDownLatch.countDown();
                    }
                });
                continue;
            }
            countDownLatch.countDown();
        }
        countDownLatch.await();
        log.info(i18nUtils.getI18nMessage(LOG_SIT_ARTICLE_PULL_DONE));
        gitService.pushRepository();
        initFlag = true;
    }
}
