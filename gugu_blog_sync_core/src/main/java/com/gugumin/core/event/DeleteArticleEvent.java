package com.gugumin.core.event;

import com.gugumin.core.pojo.Article;

import java.util.List;

/**
 * The type Delete article event.
 *
 * @author minmin
 * @date 2023 /03/26
 */
public class DeleteArticleEvent extends BaseArticleEvent {
    /**
     * Instantiates a new Delete article event.
     *
     * @param source      the source
     * @param articleList the article list
     */
    public DeleteArticleEvent(Object source, List<Article> articleList) {
        super(source, articleList);
    }
}
