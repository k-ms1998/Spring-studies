package com.fc.notice_board.notice_board.dto;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.ArticleComment;
import com.fc.notice_board.notice_board.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentDto {

    private Long id;
    private Long articleId;
    private Long parentCommentId;
    private UserAccountDto userAccountDto;
    private String content;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;


    public static ArticleCommentDto of(Long articleId, UserAccountDto userAccountDto, String content) {
        return new ArticleCommentDto(null, articleId, null, userAccountDto, content, null, null, null, null);
    }

    public static ArticleCommentDto of(Long articleId, Long parentCommentId, UserAccountDto userAccountDto, String content) {
        return new ArticleCommentDto(null, articleId, parentCommentId, userAccountDto, content, null, null, null, null);
    }

    public static ArticleCommentDto of(Long id, Long articleId, Long parentCommentId, UserAccountDto userAccountDto, String content, LocalDateTime createAt, String createdBy) {
        return new ArticleCommentDto(id, articleId, parentCommentId, userAccountDto, content, createAt, createdBy, null, null);
    }

    public static ArticleCommentDto from(ArticleComment entity) {
        return new ArticleCommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                entity.getParentCommentId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public ArticleComment toEntity(Article article, UserAccount userAccount) {
        return ArticleComment.of(
                article,
                this.getContent(),
                this.getCreatedAt(),
                this.getCreatedBy(),
                userAccount
        );
    }
}
