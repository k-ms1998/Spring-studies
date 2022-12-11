package com.fc.notice_board.notice_board.dto.response;

import com.fc.notice_board.notice_board.dto.ArticleWithCommentsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleWithCommentsResponse {

    private Long id;
    private String title;
    private String content;
    private String email;
    private String nickname;
    private String userId;
    Set<String> hashtags;
    Set<ArticleCommentResponse> articleCommentsResponse;

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.getUserAccountDto().getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.getUserAccountDto().getUserId();
        }

        return new ArticleWithCommentsResponse(
                dto.getId(),
                dto.getArticleDto().getTitle(),
                dto.getArticleDto().getContent(),
                dto.getUserAccountDto().getEmail(),
                nickname,
                dto.getUserAccountDto().getUserId(),
                Set.of(dto.getArticleDto().getHashtag()),
                dto.getArticleCommentDtos().stream()
                        .map(ArticleCommentResponse::from)
                        .collect(Collectors.toSet())
        );
    }
}
