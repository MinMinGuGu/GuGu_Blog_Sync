package com.gugumin.event;

import com.gugumin.components.SiteObserver;
import com.gugumin.config.Config;
import com.gugumin.pojo.Article;
import com.gugumin.service.IGitService;
import com.gugumin.utils.FileUtil;
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
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(r -> new Thread(r, "InitEvent-Task"));
    private final SiteObserver siteObserver;
    private final Config config;
    private final IGitService gitService;

    /**
     * Instantiates a new Init event.
     *
     * @param siteObserver the site observer
     * @param config       the config
     * @param gitService
     */
    public InitEvent(SiteObserver siteObserver, Config config, IGitService gitService) {
        this.siteObserver = siteObserver;
        this.config = config;
        this.gitService = gitService;
    }

    @PreDestroy
    private void closer() {
        EXECUTOR_SERVICE.shutdownNow();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Path repositoryPath = config.getRepositoryPath();
        if (Files.notExists(repositoryPath)) {
            log.info("工作目录下没有git仓库 准备开始初始化");
            gitService.initRepository();
            log.info("工作目录下git仓库初始化完成");
            tryPullSiteData2Repository(repositoryPath);
        }
    }

    @SneakyThrows
    private void tryPullSiteData2Repository(Path repositoryPath) {
        List<Article> articleList = siteObserver.postNotice(null, SiteObserver.NoticeType.GET_ARTICLE);
        if (CollectionUtils.isEmpty(articleList)) {
            return;
        }
        log.info("尝试将站点文章拉取到本地仓库");
        CountDownLatch countDownLatch = new CountDownLatch(articleList.size());
        for (Article article : articleList) {
            String fileName = article.getName() + ".md";
            if (Files.notExists(repositoryPath.resolve(fileName))) {
                EXECUTOR_SERVICE.execute(() -> {
                    FileUtil.write(repositoryPath.resolve(fileName), Article.parseMetaFromContext(article.getContext()).generateMetaAndContext(article));
                    log.info("成功将站点文章写入到 {}", repositoryPath.resolve(fileName));
                    countDownLatch.countDown();
                });
                continue;
            }
            countDownLatch.countDown();
        }
        countDownLatch.await();
        log.info("站点文章拉取到本地仓库完成");
        gitService.pushRepository();
    }
}
