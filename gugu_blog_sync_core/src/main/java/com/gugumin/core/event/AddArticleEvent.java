package com.gugumin.core.event;

import com.gugumin.core.pojo.Article;

import java.util.List;

/**
 * The type Add article event.
 *
 * @author minmin
 * @date 2023 /03/26
 */
public class AddArticleEvent extends BaseArticleEvent {
    /**
     * Instantiates a new Add article event.
     *
     * @param source      the source
     * @param articleList the article list
     */
    public AddArticleEvent(Object source, List<Article> articleList) {
        super(source, articleList);
    }
}
