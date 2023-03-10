package com.gugumin.halo.service.impl;

import com.gugumin.halo.pojo.request.*;
import com.gugumin.halo.pojo.response.PostsResponse;
import com.gugumin.halo.service.IHaloApi;
import com.gugumin.halo.utils.JsonUtil;
import com.gugumin.pojo.Article;
import com.gugumin.service.core.ISite;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The type Halo site.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Slf4j
@Service
public class HaloSiteImpl implements ISite {
    private static final String DEFAULT_EDITOR_TYPE = "MARKDOWN";
    private static final String SPLIT_CHAR = ",";
    private static final Pattern CATEGORIES_PATTERN = Pattern.compile("(?<=<!--categories=\\[).*(?=\\],)");
    private static final Pattern TAG_PATTERN = Pattern.compile("(?<=tags=\\[).*(?=\\]-->)");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(r -> new Thread(r, "haloSite-task"));
    private static final Object CATEGORIES_MONITOR = new Object();
    private static final Object TAG_MONITOR = new Object();
    @Resource
    private IHaloApi haloApi;


    @PreDestroy
    private void closer() {
        EXECUTOR_SERVICE.shutdownNow();
    }

    @Override
    public List<Article> getArticles() {
        log.info("开始获取halo站点的所有文章来进行托管");
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
                try {
                    String responseJson = haloApi.getPosts(id);
                    log.debug("haloApi.getPost()={}", responseJson);
                    Map<String, String> dataMap = JsonPath.read(responseJson, "$.data");
                    PostsResponse postsResponse = JsonUtil.json2Obj(JsonUtil.obj2Json(dataMap), PostsResponse.class);
                    synchronizedList.add(postsResponse.toArticle());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        log.info("等待解析halo站点的所有文章");
        countDownLatch.await();
        log.info("成功获取halo站点的所有文章");
        return synchronizedList;
    }

    private List<Integer> getAllPublishedMdId() {
        PostsQuery postsQuery = new PostsQuery();
        postsQuery.setSize(Integer.MAX_VALUE);
        postsQuery.setStatus(PostsStatus.PUBLISHED.getValue());
        JSONArray idJsonArray = JsonPath.read(haloApi.getPosts(postsQuery), "$.data.content");
        return idJsonArray.stream().map(content -> {
            String editorType = JsonPath.read(content, "$.editorType").toString();
            return DEFAULT_EDITOR_TYPE.equals(editorType) ? Integer.parseInt(JsonPath.read(content, "$.id").toString()) : null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public boolean addArticle(List<Article> articleList) {
        CountDownLatch countDownLatch = new CountDownLatch(articleList.size());
        for (Article article : articleList) {
            EXECUTOR_SERVICE.execute(() -> {
                try {
                    createOrUpdatePosts(article, true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        return true;
    }

    private void createOrUpdatePosts(Article article, boolean isCreate) {
        Map<String, List<String>> metaList = getCategoriesAndTagsData(article);
        List<String> categoriesNameList = metaList.get("categories");
        List<Integer> categoriesIdList = createCategoriesList(categoriesNameList);
        List<String> tagNameList = metaList.get("tags");
        List<Integer> tagIdList = createTagList(tagNameList);
        PostsRequest postsRequest = createPostRequest(article, categoriesIdList, tagIdList);
        if (isCreate) {
            haloApi.postPosts(postsRequest);
        } else {
            PostsQuery postsQuery = new PostsQuery();
            postsQuery.setKeyword(article.getName());
            String getPostsJson = haloApi.getPosts(postsQuery);
            Integer postsId = JsonPath.read(getPostsJson, "$.data.content[0].id");
            haloApi.putPosts(postsId, postsRequest);
        }
    }

    private PostsRequest createPostRequest(Article article, List<Integer> categoriesIdList, List<Integer> tagIdList) {
        PostsRequest postsRequest = new PostsRequest();
        postsRequest.setCategoryIds(categoriesIdList);
        postsRequest.setTagIds(tagIdList);
        String context = article.getContext();
        postsRequest.setOriginalContent(context);
        postsRequest.setTitle(article.getName());
        postsRequest.setEditorType(DEFAULT_EDITOR_TYPE);
        postsRequest.setStatus(PostsStatus.PUBLISHED.getValue());
        postsRequest.setKeepRaw(false);
        return postsRequest;
    }

    private synchronized List<Integer> createTagList(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            return Collections.emptyList();
        }
        synchronized (TAG_MONITOR) {
            return getIntegerListFromCategoryOrTag(tagNameList, false);
        }
    }

    private List<Integer> createCategoriesList(List<String> categoriesNameList) {
        if (CollectionUtils.isEmpty(categoriesNameList)) {
            return Collections.emptyList();
        }
        synchronized (CATEGORIES_MONITOR) {
            return getIntegerListFromCategoryOrTag(categoriesNameList, true);
        }
    }

    private List<Integer> getIntegerListFromCategoryOrTag(List<String> nameList, boolean isCategory) {
        String getResponseJson = isCategory ? haloApi.getCategories() : haloApi.getTags();
        JSONArray categoriesNameJsonArray = JsonPath.read(getResponseJson, "$..name");
        List<String> haloNameList = categoriesNameJsonArray.stream().map(Object::toString).collect(Collectors.toList());
        List<String> needCreateList = nameList.stream().filter(categoriesName -> !haloNameList.contains(categoriesName)).collect(Collectors.toList());
        List<Integer> idList = new LinkedList<>();
        if (CollectionUtils.isEmpty(needCreateList)) {
            JSONArray dataJsonArray = JsonPath.read(getResponseJson, "$.data");
            for (Object categoriesObject : dataJsonArray) {
                if (categoriesObject instanceof Map<?, ?>) {
                    Map<?, ?> categoriesMap = (Map<?, ?>) categoriesObject;
                    if (nameList.contains(categoriesMap.get("name").toString())) {
                        idList.add((Integer) categoriesMap.get("id"));
                    }
                }
            }
            return idList;
        }
        for (String needCreate : needCreateList) {
            String postResponseJson;
            if (isCategory) {
                CategoryRequest categoryRequest = new CategoryRequest();
                categoryRequest.setName(needCreate);
                postResponseJson = haloApi.postCategory(categoryRequest);
            } else {
                TagsRequest tagsRequest = new TagsRequest();
                tagsRequest.setName(needCreate);
                postResponseJson = haloApi.postTags(tagsRequest);
            }
            Integer categoriesId = JsonPath.read(postResponseJson, "$.data.id");
            idList.add(categoriesId);
        }
        return idList;
    }

    private Map<String, List<String>> getCategoriesAndTagsData(Article article) {
        Map<String, List<String>> metaList = new LinkedHashMap<>();
        metaList.put("categories", new LinkedList<>());
        metaList.put("tags", new LinkedList<>());
        Matcher categoriesMatcher = CATEGORIES_PATTERN.matcher(article.getContext());
        if (categoriesMatcher.find()) {
            String categoriesGroup = categoriesMatcher.group();
            List<String> categoriesNameList = Arrays.stream(categoriesGroup.split(SPLIT_CHAR)).map(String::trim).collect(Collectors.toList());
            metaList.get("categories").addAll(categoriesNameList);
        }
        Matcher tagMatcher = TAG_PATTERN.matcher(article.getContext());
        if (tagMatcher.find()) {
            String tagGroup = tagMatcher.group();
            List<String> tagNameList = Arrays.stream(tagGroup.split(SPLIT_CHAR)).map(String::trim).collect(Collectors.toList());
            metaList.get("tags").addAll(tagNameList);
        }
        return metaList;
    }

    @SneakyThrows
    @Override
    public boolean removeArticle(List<Article> articleList) {
        List<String> titleList = articleList.stream().map(Article::getName).collect(Collectors.toList());
        CountDownLatch countDownLatch = new CountDownLatch(titleList.size());
        List<Integer> postIdList = Collections.synchronizedList(new LinkedList<>());
        for (String title : titleList) {
            EXECUTOR_SERVICE.execute(() -> {
                try {
                    PostsQuery postsQuery = new PostsQuery();
                    postsQuery.setKeyword(title);
                    String responseJson = haloApi.getPosts(postsQuery);
                    Integer id = JsonPath.read(responseJson, "$.data.content[0].id");
                    postIdList.add(id);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        haloApi.delPosts(postIdList);
        return true;
    }

    @SneakyThrows
    @Override
    public boolean updateArticle(List<Article> articleList) {
        CountDownLatch countDownLatch = new CountDownLatch(articleList.size());
        for (Article article : articleList) {
            EXECUTOR_SERVICE.execute(() -> {
                try {
                    createOrUpdatePosts(article, false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        return true;
    }
}
