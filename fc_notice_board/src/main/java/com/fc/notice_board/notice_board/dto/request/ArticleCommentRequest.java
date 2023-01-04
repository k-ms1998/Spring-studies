package com.fc.notice_board.notice_board.dto.request;

import com.fc.notice_board.notice_board.dto.ArticleCommentDto;
import com.fc.notice_board.notice_board.dto.UserAccountDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentRequest {

    private Long articleId;
    private Long parentCommentId;
    private String content;

    public static ArticleCommentRequest of(Long articleId, String content) {
        return new ArticleCommentRequest(articleId, null, content);
    }

    public static ArticleCommentRequest of(Long articleId, Long parentCommentId, String content) {
        return new ArticleCommentRequest(articleId, parentCommentId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto) {
        return ArticleCommentDto.of(
                this.getArticleId(),
                this.getParentCommentId(),
                userAccountDto,
                this.getContent()
        );
    }

}
