package com.gugumin.core.event;

import com.gugumin.core.pojo.Article;

import java.util.List;

/**
 * The type Update article event.
 *
 * @author minmin
 * @date 2023 /03/26
 */
public class UpdateArticleEvent extends BaseArticleEvent {
    /**
     * Instantiates a new Update article event.
     *
     * @param source      the source
     * @param articleList the article list
     */
    public UpdateArticleEvent(Object source, List<Article> articleList) {
        super(source, articleList);
    }
}
