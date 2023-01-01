package com.fc.notice_board.notice_board.repository;

import com.fc.notice_board.notice_board.domain.Hashtag;
import com.fc.notice_board.notice_board.domain.QHashtag;
import com.fc.notice_board.notice_board.repository.querydsl.ArticleRepositoryCustom;
import com.fc.notice_board.notice_board.repository.querydsl.HashtagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HashtagRepository extends
        JpaRepository<Hashtag, Long>,
        QuerydslPredicateExecutor<Hashtag>, // 해당 엔티티(Article)에 있는 모든 필드들에 대해서 검색 기능 추가
        HashtagRepositoryCustom {

    Optional<Hashtag> findByHashtagName(String hashtagName);
    List<Hashtag> findByHashtagNameIn(Set<String> hashtagNames);
}
