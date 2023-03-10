package com.gugumin.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author minmin
 * @date 2023/03/11
 */
@Data
public class Meta {
    private List<Category> categories;
    private List<Tag> tags;

    @Data
    public static class Category {
        private String parent;
        private String name;
    }

    @Data
    public static class Tag {
        private String name;
    }
}
