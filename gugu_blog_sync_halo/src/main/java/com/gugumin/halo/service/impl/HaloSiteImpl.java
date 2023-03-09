package com.gugumin.halo.service.impl;

import com.dtflys.forest.Forest;
import com.gugumin.halo.pojo.response.Posts;
import com.gugumin.halo.service.IHaloApi;
import com.gugumin.halo.utils.JsonUtil;
import com.gugumin.pojo.Article;
import com.gugumin.service.core.ISite;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author minmin
 * @date 2023/03/08
 */
@Slf4j
@Service
public class HaloSiteImpl implements ISite {
    private static final String DEFAULT_EDITOR_TYPE = "MARKDOWN";

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(r -> new Thread(r, "HaloSiteImpl-Task"));
    @Resource
    private IHaloApi haloApi;

    @PreDestroy
    private void closer() {
        EXECUTOR_SERVICE.shutdownNow();
    }

    @Override
    public List<Article> getArticles() {
        log.info("开始获取halo站点的所有文章");
        List<Integer> idList = getAllPublishedMdId();
        log.info("成功获取halo站点的所有文章id");
        log.debug("getAllPublishedId={}", idList);
        return analyzeArticleList(idList);
    }

    @SneakyThrows
    private List<Article> analyzeArticleList(List<Integer> idList) {
        CountDownLatch countDownLatch = new CountDownLatch(idList.size());
        List<Article> synchronizedList = Collections.synchronizedList(new LinkedList<>());
        for (Integer id : idList) {
            EXECUTOR_SERVICE.execute(() -> {
                Forest.config().setVariableValue("postId", id);
                String responseJson = haloApi.getPost();
                log.debug("haloApi.getPost()={}", responseJson);
                Map<String,String> dataMap = JsonPath.read(responseJson, "$.data");
                Posts posts = JsonUtil.json2Obj(JsonUtil.obj2Json(dataMap), Posts.class);
                synchronizedList.add(posts.toArticle());
                countDownLatch.countDown();
            });
        }
        log.info("等待解析halo站点的所有文章");
        countDownLatch.await();
        log.info("成功获取halo站点的所有文章");
        return synchronizedList;
    }

    private List<Integer> getAllPublishedMdId() {
        JSONArray idJsonArray = JsonPath.read(haloApi.posts(), "$.data.content");
        return idJsonArray.stream().map(content -> {
            String editorType = JsonPath.read(content, "$.editorType").toString();
            return DEFAULT_EDITOR_TYPE.equals(editorType) ? Integer.parseInt(JsonPath.read(content, "$.id").toString()) : null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean addArticle(List<Article> articleList) {
        return false;
    }

    @Override
    public boolean removeArticle(List<Article> articleList) {
        return false;
    }

    @Override
    public boolean updateArticle(List<Article> articleList) {
        return false;
    }
}
