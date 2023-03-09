package com.gugumin.halo.service;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.gugumin.halo.pojo.request.Account;

/**
 * @author minmin
 * @date 2023/03/08
 */
public interface IHaloApi {
    @Post("${url}/api/admin/login")
    String login(@JSONBody Account account);
    @Get(url = "${url}/api/admin/posts", headers = {"ADMIN-Authorization: ${token}"}, data = {"size=65535", "status=PUBLISHED"})
    String posts();
    @Get(url = "${url}/api/admin/posts/${postId}", headers = {"ADMIN-Authorization: ${token}"})
    String getPost();
}
