package com.fc.notice_board.notice_board.repository;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.QArticle;
import com.fc.notice_board.notice_board.dto.ArticleDto;
import com.fc.notice_board.notice_board.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ArticleRepository
        extends JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>, // 해당 엔티티(Article)에 있는 모든 필드들에 대해서 검색 기능 추가
        QuerydslBinderCustomizer<QArticle>, //
        ArticleRepositoryCustom
{

    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByTitleContaining(String title, Pageable pageable);
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);

    void deleteByIdAndUserAccount_UserId(Long articleId, String userId);

    /**
     * Article 의 모든 필드가 아닌, 원하는 필드들만 검색하기 위해 설정해주는 메서드
     * <p>
     * ex: http://localhost:8080/api/articles?title=nullam => title 에 대소문자 구별하지 않고 'nullam' 값을 가진 데이터들을 가져옴
     */
    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);   // including 에 포함되지 않은 필드들은 검색할때 제외하는 것을 true 로 해줌 (필수)
        bindings.including(root.title, root.content, root.hashtags, root.createdAt, root.createdBy); // 검색을 허용할 필드들만 설정

        /**
         * title, hashtag, createdBy 를 검색할때, 대소문자를 구별하지 않고 {parameter} like '%{search}%' 이런식으로 검색이 되도록 설정
         * ex: SELECT *FROM article WHERE lower(title) like '%hello%';
         */
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtags.any().hashtagName).first(StringExpression::contains);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);

        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
    }

}
