package com.fc.notice_board.notice_board.repository;

import com.fc.notice_board.notice_board.domain.ArticleComment;
import com.fc.notice_board.notice_board.domain.QArticleComment;
import com.fc.notice_board.notice_board.dto.ArticleCommentDto;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ArticleCommentRepository
        extends JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>,
        QuerydslBinderCustomizer<QArticleComment>
{

    List<ArticleComment> findByArticle_Id(Long articleId);

    /**
     * ArticleComment 의 모든 필드가 아닌, 원하는 필드들만 검색하기 위해 설정해주는 메서드
     */
    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        bindings.excludeUnlistedProperties(true);   // including 에 포함되지 않은 필드들은 검색할때 제외하는 것을 true 로 해줌 (필수)
        bindings.including(root.content, root.createdAt, root.createdBy); // 검색을 허용할 필드들만 설정

        /**
         * content, createdBy 를 검색할때, {parameter} like '%{search}%' 이런식으로 검색이 되도록 설정
         * ex: SELECT *FROM article_comment WHERE content like '%hello%';
         */
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);

        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
    }
}
