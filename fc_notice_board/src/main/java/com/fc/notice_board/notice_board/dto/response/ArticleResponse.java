package com.fc.notice_board.notice_board.dto.response;

import com.fc.notice_board.notice_board.dto.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String hashtag;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.getUserAccountDto().getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.getUserAccountDto().getUserId();
        }

        return new ArticleResponse(
                dto.getId(),
                dto.getTitle(),
                dto.getContent(),
                dto.getHashtag(),
                dto.getUserAccountDto().getEmail(),
                nickname,
                dto.getCreatedAt()
        );
    }
}
