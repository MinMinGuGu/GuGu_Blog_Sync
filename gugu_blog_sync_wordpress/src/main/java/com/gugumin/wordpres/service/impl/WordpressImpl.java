package com.gugumin.wordpres.service.impl;

import com.gugumin.pojo.Article;
import com.gugumin.service.core.ISite;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordpressImpl implements ISite {
    @Override
    public List<Article> getArticles() {
        return ISite.super.getArticles();
    }

    @Override
    public boolean addArticle(List<Article> articleList) {
        return false;
    }

    @Override
    public boolean removeArticle(List<Article> articleList) {
        return false;
    }

    @Override
    public boolean updateArticle(List<Article> articleList) {
        return false;
    }
}
