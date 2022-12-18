package com.fc.notice_board.notice_board.repository.querydsl.impl;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.QArticle;
import com.fc.notice_board.notice_board.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {


    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;

        return from(article)
                .distinct()
                .select(article.hashtag)
                .where(article.hashtag.isNotNull())
                .fetch();

    }
}
