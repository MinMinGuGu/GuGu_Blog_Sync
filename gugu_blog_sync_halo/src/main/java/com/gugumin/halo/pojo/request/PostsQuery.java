package com.gugumin.halo.pojo.request;

import lombok.Data;

import java.util.List;

/**
 * The type Posts query.
 *
 * @author minmin
 * @date 2023 /03/10
 */
@Data
public class PostsQuery {
    private Integer categoryId;
    private String keyword;
    private Integer page;
    private Integer size;
    private List<String> sort;
    /**
     * Enum: "DRAFT" "INTIMATE" "PUBLISHED" "RECYCLE"
     */
    private String status;
    private List<String> statuses;
    private Boolean more;
}
