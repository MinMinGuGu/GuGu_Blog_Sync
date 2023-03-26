package com.gugumin.event;

import com.gugumin.pojo.Article;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * The type Abs article event.
 *
 * @author minmin
 * @date 2023 /03/26
 */
public abstract class BaseArticleEvent extends ApplicationEvent {
    /**
     * The Article list.
     */
    @Getter
    protected final List<Article> articleList;

    /**
     * Instantiates a new Abs article event.
     *
     * @param source      the source
     * @param articleList the article list
     */
    public BaseArticleEvent(Object source, List<Article> articleList) {
        super(source);
        this.articleList = articleList;
    }
}