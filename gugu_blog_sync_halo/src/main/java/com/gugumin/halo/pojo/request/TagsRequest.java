package com.gugumin.halo.pojo.request;

import lombok.Data;

/**
 * The type Tags request.
 *
 * @author minmin
 * @date 2023 /03/10
 */
@Data
public class TagsRequest {
    private String color;
    private String name;
    private String slug;
    private String thumbnail;
}
