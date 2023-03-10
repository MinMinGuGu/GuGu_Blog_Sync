package com.gugumin.halo.pojo.request;

import lombok.Data;

/**
 * The type Category request.
 *
 * @author minmin
 * @date 2023 /03/10
 */
@Data
public class CategoryRequest {
    private String description;
    private Integer id;
    private String name;
    private Integer parentId;
    private String password;
    private Integer priority;
    private String slug;
    private String thumbnail;
}
