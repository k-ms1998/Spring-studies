package com.fc.notice_board.notice_board.dto.request;

import com.fc.notice_board.notice_board.dto.ArticleDto;
import com.fc.notice_board.notice_board.dto.UserAccountDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequest {

    private String title;
    private String content;
    private String hashtag;

    public static ArticleRequest of(String title, String content, String hashtag) {
        return new ArticleRequest(title, content, hashtag);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto) {
        return ArticleDto.of(
                this.getTitle(),
                this.getContent(),
                this.getHashtag(),
                userAccountDto
        );
    }
}
