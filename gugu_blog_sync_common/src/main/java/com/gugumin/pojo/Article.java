package com.gugumin.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Article.
 *
 * @author minmin
 * @date 2023 /03/06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private String name;
    private String context;
}
