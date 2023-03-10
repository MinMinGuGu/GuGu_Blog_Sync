package com.gugumin.halo.service;

import com.dtflys.forest.annotation.*;
import com.gugumin.halo.pojo.request.*;

import java.util.List;

/**
 * The interface Halo api.
 *
 * @author minmin
 * @date 2023 /03/08
 */
public interface IHaloApi {
    /**
     * Login string.
     *
     * @param accountRequest the account request
     * @return the string
     */
    @Post("${url}/api/admin/login")
    String login(@JSONBody AccountRequest accountRequest);

    /**
     * Gets posts.
     *
     * @param postsQuery the posts query
     * @return the posts
     */
    @Get(url = "${url}/api/admin/posts", headers = {"ADMIN-Authorization: ${token}"})
    String getPosts(@Query PostsQuery postsQuery);

    /**
     * Gets post.
     *
     * @param postId the post id
     * @return the post
     */
    @Get(url = "${url}/api/admin/posts/{postId}", headers = {"ADMIN-Authorization: ${token}"})
    String getPosts(@Var("postId") Integer postId);

    /**
     * Del posts string.
     *
     * @param idList the id list
     * @return the string
     */
    @Delete(url = "${url}/api/admin/posts", headers = {"ADMIN-Authorization: ${token}"})
    void delPosts(@JSONBody List<Integer> idList);

    /**
     * Post posts string.
     *
     * @param postsRequest the posts request
     */
    @Post(url = "${url}/api/admin/posts", headers = {"ADMIN-Authorization: ${token}"})
    void postPosts(@JSONBody PostsRequest postsRequest);

    /**
     * Put post string.
     *
     * @param postId       the post id
     * @param postsRequest the posts request
     */
    @Put(url = "${url}/api/admin/posts/{postId}", headers = {"ADMIN-Authorization: ${token}"})
    void putPosts(@Var("postId") Integer postId, @JSONBody PostsRequest postsRequest);

    /**
     * Gets categories.
     *
     * @return the categories
     */
    @Get(url = "${url}/api/admin/categories", headers = {"ADMIN-Authorization: ${token}"})
    String getCategories();

    /**
     * Post category string.
     *
     * @param categoryRequest the category request
     * @return the string
     */
    @Post(url = "${url}/api/admin/categories", headers = {"ADMIN-Authorization: ${token}"})
    String postCategory(@JSONBody CategoryRequest categoryRequest);

    /**
     * Gets tags.
     *
     * @return the tags
     */
    @Get(url = "${url}/api/admin/tags", headers = {"ADMIN-Authorization: ${token}"})
    String getTags();

    /**
     * Post tgas string.
     *
     * @param tagsRequest the tag request
     * @return the string
     */
    @Post(url = "${url}/api/admin/tags", headers = {"ADMIN-Authorization: ${token}"})
    String postTags(@JSONBody TagsRequest tagsRequest);
}
