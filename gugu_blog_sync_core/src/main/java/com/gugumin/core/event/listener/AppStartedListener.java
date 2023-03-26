package com.gugumin.core.event.listener;

import com.gugumin.core.components.I18nHelper;
import com.gugumin.core.components.SpringHelper;
import com.gugumin.core.config.CoreConfig;
import com.gugumin.core.pojo.Article;
import com.gugumin.core.service.IGitService;
import com.gugumin.core.service.IHandlerInitSite;
import com.gugumin.core.utils.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
public class AppStartedListener {
    private static final String LOG_GIT_INIT_POST = "log_git_init_post";
    private static final String LOG_GIT_INIT_DONE = "log_git_init_done";
    private static final String LOG_SIT_ARTICLE_PULL = "log_site_article_pull";
    private static final String LOG_SIT_ARTICLE_PULL_WRITE = "log_site_article_pull_write";
    private static final String LOG_SIT_ARTICLE_PULL_DONE = "log_site_article_pull_done";
    private static final String LOG_WATCH_WEBHOOK = "log_watch_webhook";

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(r -> new Thread(r, "InitEvent-Task"));
    private static boolean initFlag = false;
    private final SpringHelper springHelper;
    private final CoreConfig coreConfig;
    private final IGitService gitService;
    private final I18nHelper i18nHelper;


    /**
     * Instantiates a new Init event.
     *
     * @param springHelper the spring helper
     * @param coreConfig   the config
     * @param gitService   gitService
     * @param i18nHelper   i18nUtils
     */
    public AppStartedListener(SpringHelper springHelper, CoreConfig coreConfig, IGitService gitService, I18nHelper i18nHelper) {
        this.springHelper = springHelper;
        this.coreConfig = coreConfig;
        this.gitService = gitService;
        this.i18nHelper = i18nHelper;
    }

    @PreDestroy
    private void closer() {
        EXECUTOR_SERVICE.shutdownNow();
    }

    /**
     * On application event.
     */
    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationEvent() {
        Path repositoryPath = coreConfig.getRepositoryPath();
        if (Files.notExists(repositoryPath)) {
            log.info(i18nHelper.getI18nMessage(LOG_GIT_INIT_POST));
            gitService.initRepository();
            log.info(i18nHelper.getI18nMessage(LOG_GIT_INIT_DONE));
            tryPullSiteData2Repository(repositoryPath);
        }
        log.info(i18nHelper.getI18nMessage(LOG_WATCH_WEBHOOK));
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
        List<com.gugumin.core.pojo.Article> articleList = getSiteArticleList();
        if (CollectionUtils.isEmpty(articleList)) {
            return;
        }
        log.info(i18nHelper.getI18nMessage(LOG_SIT_ARTICLE_PULL));
        CountDownLatch countDownLatch = new CountDownLatch(articleList.size());
        for (com.gugumin.core.pojo.Article article : articleList) {
            String fileName = article.getName() + ".md";
            if (Files.notExists(repositoryPath.resolve(fileName))) {
                EXECUTOR_SERVICE.execute(() -> {
                    try {
                        FileUtil.write(repositoryPath.resolve(fileName), article.getMetaType().generateMetaAndContext(article));
                        log.info(i18nHelper.getI18nMessage(LOG_SIT_ARTICLE_PULL_WRITE), repositoryPath.resolve(fileName));
                    } finally {
                        countDownLatch.countDown();
                    }
                });
                continue;
            }
            countDownLatch.countDown();
        }
        countDownLatch.await();
        log.info(i18nHelper.getI18nMessage(LOG_SIT_ARTICLE_PULL_DONE));
        gitService.pushRepository();
        initFlag = true;
    }

    private List<com.gugumin.core.pojo.Article> getSiteArticleList() {
        List<Article> articles = new LinkedList<>();
        Map<String, IHandlerInitSite> iHandlerInitSiteMap = springHelper.getApplicationContext().getBeansOfType(IHandlerInitSite.class);
        for (Map.Entry<String, IHandlerInitSite> siteEntry : iHandlerInitSiteMap.entrySet()) {
            IHandlerInitSite site = siteEntry.getValue();
            articles.addAll(site.getArticles());
        }
        return articles;
    }
}
