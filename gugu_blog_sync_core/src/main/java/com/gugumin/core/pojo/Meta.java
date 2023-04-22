package com.gugumin.core.pojo;

import lombok.Data;

import java.util.List;

/**
 * The type Meta.
 *
 * @author minmin
 * @date 2023 /03/11
 */
@Data
public class Meta {
    private String title;
    private List<Category> categories;
    private List<Tag> tags;
    private String summary;

    /**
     * The type Category.
     */
    @Data
    public static class Category {
        private String parent;
        private String name;
    }

    /**
     * The type Tag.
     */
    @Data
    public static class Tag {
        private String name;
    }
}
