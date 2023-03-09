package org.example.service.impl;

import com.gugumin.pojo.Article;
import com.gugumin.service.core.ISite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author minmin
 * @date 2023/03/08
 */
@Slf4j
@Service
public class ExampleSiteImpl implements ISite {
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
