package com.fc.notice_board.notice_board.repository.querydsl.impl;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.Hashtag;
import com.fc.notice_board.notice_board.domain.QArticle;
import com.fc.notice_board.notice_board.domain.QHashtag;
import com.fc.notice_board.notice_board.repository.querydsl.ArticleRepositoryCustom;
import com.fc.notice_board.notice_board.repository.querydsl.HashtagRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Collection;
import java.util.List;

public class HashtagRepositoryCustomImpl extends QuerydslRepositorySupport implements HashtagRepositoryCustom {


    public HashtagRepositoryCustomImpl() {
        super(Hashtag.class);
    }
}
