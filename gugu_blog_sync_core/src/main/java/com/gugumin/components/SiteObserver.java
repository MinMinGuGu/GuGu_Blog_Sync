package com.gugumin.components;

import com.gugumin.pojo.Article;
import com.gugumin.service.core.ISite;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Site observer.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Slf4j
@Component
@Lazy
public class SiteObserver {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool((r) -> new Thread(r, "siteObs-Task"));
    private static final int DEFAULT_WAIT_THREAD_TIME = 10;
    private final List<ISite> siteList = new LinkedList<>();
    private final SpringHelper springHelper;

    /**
     * Instantiates a new Site observer.
     *
     * @param springHelper the spring helper
     */
    public SiteObserver(SpringHelper springHelper) {
        this.springHelper = springHelper;
    }

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        Map<String, ISite> iSiteMap = springHelper.getApplicationContext().getBeansOfType(ISite.class);
        iSiteMap.forEach((key, value) -> {
            siteList.add(value);
            log.info("加载 {} 完成", value.getClass().getName());
        });
    }

    @PreDestroy
    public void closer() {
        log.info("正在停止 SiteObserver 通知调用线程池");
        EXECUTOR_SERVICE.shutdown();
        try {
            if (!EXECUTOR_SERVICE.awaitTermination(DEFAULT_WAIT_THREAD_TIME, TimeUnit.SECONDS)) {
                EXECUTOR_SERVICE.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("SiteObserver 线程池已经关闭");
    }

    /**
     * Post notice list.
     *
     * @param articleList the article list
     * @param noticeType  the notice type
     * @return the list
     */
    @SneakyThrows
    public List<Article> postNotice(List<Article> articleList, NoticeType noticeType) {
        if (CollectionUtils.isEmpty(articleList) && noticeType != NoticeType.GET_ARTICLE) {
            return Collections.emptyList();
        }
        switch (noticeType) {
            case ADD_ARTICLE: {
                siteList.forEach(item -> EXECUTOR_SERVICE.execute(() -> {
                    try {
                        item.addArticle(articleList);
                    } catch (Exception e) {
                        log.error("执行 {} 的addArticle方法时 它抛出了异常", item.getClass().getName(), e);
                    }
                }));
                return Collections.emptyList();
            }
            case REMOVE_ARTICLE: {
                siteList.forEach(item -> EXECUTOR_SERVICE.execute(() -> {
                    try {
                        item.removeArticle(articleList);
                    } catch (Exception e) {
                        log.error("执行 {} 的removeArticle方法时 它抛出了异常", item.getClass().getName(), e);
                    }
                }));
                return Collections.emptyList();
            }
            case UPDATE_ARTICLE: {
                siteList.forEach(item -> EXECUTOR_SERVICE.execute(() -> {
                    try {
                        item.updateArticle(articleList);
                    } catch (Exception e) {
                        log.error("执行 {} 的updateArticle方法时 它抛出了异常", item.getClass().getName(), e);
                    }
                }));
                return Collections.emptyList();
            }
            case GET_ARTICLE: {
                List<Article> articles = new LinkedList<>();
                for (ISite site : siteList) {
                    articles.addAll(site.getArticles());
                }
                return articles;
            }
            default: {
                return Collections.emptyList();
            }
        }
    }

    /**
     * The enum Notice type.
     */
    public enum NoticeType {
        /**
         * 添加文章
         */
        ADD_ARTICLE,
        /**
         * 移除文章
         */
        REMOVE_ARTICLE,
        /**
         * 更新文章
         */
        UPDATE_ARTICLE,
        /**
         * 获取文章
         */
        GET_ARTICLE
    }
}
