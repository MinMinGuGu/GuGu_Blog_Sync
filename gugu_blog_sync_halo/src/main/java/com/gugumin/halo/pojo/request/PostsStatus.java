package com.gugumin.halo.pojo.request;

import lombok.Getter;

/**
 * The enum Posts status.
 *
 * @author minmin
 * @date 2023 /03/10
 */
@Getter
public enum PostsStatus {
    /**
     * 草稿
     */
    DRAFT("DRAFT"),
    /**
     * 私密
     */
    INTIMATE("INTIMATE"),
    /**
     * 发布
     */
    PUBLISHED("PUBLISHED"),
    /**
     * 回收
     */
    RECYCLE("RECYCLE");
    private final String value;

    PostsStatus(String value) {
        this.value = value;
    }
}
