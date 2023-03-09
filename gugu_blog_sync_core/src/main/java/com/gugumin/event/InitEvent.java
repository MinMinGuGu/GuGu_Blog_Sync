package com.gugumin.event;

import com.gugumin.components.SiteObserver;
import com.gugumin.config.Config;
import com.gugumin.pojo.Article;
import com.gugumin.utils.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.io.IOException;
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

    /**
     * Instantiates a new Init event.
     *
     * @param siteObserver the site observer
     * @param config       the config
     */
    public InitEvent(SiteObserver siteObserver, Config config) {
        this.siteObserver = siteObserver;
        this.config = config;
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
            initRepository(repositoryPath);
            tryPullSiteData2Repository(repositoryPath);
        }
    }

    @SneakyThrows
    private void tryPullSiteData2Repository(Path repositoryPath) {
        List<Article> articleList = siteObserver.postNotice(null, SiteObserver.NoticeType.GET_ARTICLE);
        if (CollectionUtils.isEmpty(articleList)) {
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(articleList.size());
        for (Article article : articleList) {
            String fileName = article.getName() + ".md";
            if (Files.notExists(repositoryPath.resolve(fileName))) {
                EXECUTOR_SERVICE.execute(() -> {
                    FileUtil.write(repositoryPath.resolve(fileName), article.getContext());
                    log.info("成功将站点文章写入到 {}", repositoryPath.resolve(fileName));
                    countDownLatch.countDown();
                });
                continue;
            }
            countDownLatch.countDown();
        }
        countDownLatch.await();
        log.info("准备开始将本地仓库同步到git远程仓库");
        try {
            Git open = Git.open(repositoryPath.toFile());
            open.add().addFilepattern(".").call();
            open.commit().setMessage("提交站点文章").call();
            open.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getGit().getUsername(), config.getGit().getToken()))
                    .setPushAll()
                    .call();
            open.close();
            log.info("成功将本地仓库同步到git远程仓库");
        } catch (IOException | GitAPIException e) {
            log.error("将站点文章同步到git远程仓库出错");
            throw new RuntimeException(e);
        }
    }

    private void initRepository(Path repositoryPath) {
        try {
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(config.getGit().getRepository())
                    .setDirectory(repositoryPath.toFile());
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getGit().getUsername(), config.getGit().getToken()));
            cloneCommand.call().close();
            log.info("克隆git仓库成功");
            Git open = Git.open(repositoryPath.toFile());
            open.checkout()
                    .setName("main")
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .setStartPoint("origin/main")
                    .call();
            open.close();
            log.info("本地分支已经关联远端分支");
        } catch (GitAPIException | IOException e) {
            log.error("初始化git仓库时失败");
            throw new RuntimeException(e);
        }
    }
}
