package com.gugumin.event.listener;

import com.gugumin.core.event.AddArticleEvent;
import com.gugumin.core.pojo.Article;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The type Handler event.
 *
 * @author minmin
 * @date 2023 /03/26
 */
@Component
public class HandlerEvent {
    /**
     * Handler add event.
     *
     * @param addArticleEvent the add article event
     */
    @EventListener(AddArticleEvent.class)
    public void handlerAddEvent(AddArticleEvent addArticleEvent) {
        Object source = addArticleEvent.getSource();
        System.out.println("source = " + source);
        List<Article> articleList = addArticleEvent.getArticleList();
        System.out.println("articleList = " + articleList);
    }
}
