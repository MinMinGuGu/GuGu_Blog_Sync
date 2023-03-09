package com.gugumin.service.core;


import com.gugumin.pojo.Article;

import java.util.Collections;
import java.util.List;

/**
 * The interface Site.
 *
 * @author minmin
 * @date 2023 /03/08
 */
public interface ISite {
    /**
     * Get articles list.
     *
     * @return the list
     */
    default List<Article> getArticles() {
        return Collections.emptyList();
    }

    /**
     * Add article boolean.
     *
     * @param articleList the article list
     * @return the boolean
     */
    boolean addArticle(List<Article> articleList);

    /**
     * Remove article boolean.
     *
     * @param articleList the article list
     * @return the boolean
     */
    boolean removeArticle(List<Article> articleList);

    /**
     * Update article boolean.
     *
     * @param articleList the article list
     * @return the boolean
     */
    boolean updateArticle(List<Article> articleList);
}
