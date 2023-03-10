package com.gugumin.halo.pojo.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The type Posts request.
 *
 * @author minmin
 * @date 2023 /03/10
 */
@Data
public class PostsRequest {
    private List<Integer> categoryIds;
    private String content;
    private String createTime;
    private Boolean disallowComment;
    private String editorType;
    private Boolean keepRaw;
    private String metaDescription;
    private String metaKeywords;
    private List<Metas> metas;
    private String originalContent;
    private String password;
    private String slug;
    private String status;
    private String summary;
    private List<Integer> tagIds;
    private String template;
    private String thumbnail;
    private String title;
    private Integer topPriority;

    /**
     * The type Metas.
     */
    @NoArgsConstructor
    @Data
    public static class Metas {
        private String key;
        private Integer postId;
        private String value;
    }
}
