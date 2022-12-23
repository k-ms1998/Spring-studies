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
    private String content;

    public static ArticleCommentRequest of(Long articleId, String content) {
        return new ArticleCommentRequest(articleId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto) {
        return ArticleCommentDto.of(
                this.getArticleId(),
                userAccountDto,
                this.getContent()
        );
    }

}
