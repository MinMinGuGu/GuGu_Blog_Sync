package com.gugumin.halo.pojo.response;

import com.gugumin.pojo.Article;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author minmin
 * @date 2023/03/08
 */
@Data
public class Posts {
    private Integer id;
    private String title;
    private String status;
    private String slug;
    private String editorType;
    private Long updateTime;
    private Long createTime;
    private Long editTime;
    private Object metaKeywords;
    private Object metaDescription;
    private String fullPath;
    private String summary;
    private String thumbnail;
    private Integer visits;
    private Boolean disallowComment;
    private String password;
    private String template;
    private Integer topPriority;
    private Integer likes;
    private Integer wordCount;
    private Boolean inProgress;
    private String originalContent;
    private String content;
    private Integer commentCount;
    private List<Integer> tagIds;
    private List<Tags> tags;
    private List<Integer> categoryIds;
    private List<Categories> categories;
    private List<?> metaIds;
    private List<?> metas;
    private String formatContent;
    private Boolean topped;

    public Article toArticle() {
        String newContent = "<!--" +
                "categories=" +
                categories.stream().map(Categories::getName).collect(Collectors.toList()) +
                "," +
                "tags=" +
                tags.stream().map(Tags::getName).collect(Collectors.toList()) +
                "-->" +
                System.lineSeparator() +
                originalContent;
        return new Article(title, newContent);
    }

    @Data
    public static class Tags {
        private Integer id;
        private String name;
        private String slug;
        private String color;
        private String thumbnail;
        private Long createTime;
        private String fullPath;
    }

    @Data
    public static class Categories {
        private Integer id;
        private String name;
        private String slug;
        private String description;
        private String thumbnail;
        private Integer parentId;
        private Object password;
        private Long createTime;
        private String fullPath;
        private Integer priority;
    }
}
