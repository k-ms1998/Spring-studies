package com.fc.notice_board.notice_board.dto.response;

import com.fc.notice_board.notice_board.domain.Hashtag;
import com.fc.notice_board.notice_board.dto.ArticleWithCommentsDto;
import com.fc.notice_board.notice_board.dto.HashtagDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
    Set<Hashtag> hashtag;
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
                dto.getArticleDto().getCreatedAt(),
                dto.getArticleDto().getHashtagsDtos()
                        .stream().map(HashtagDto::hashtagName)
                        .collect(Collectors.toUnmodifiableSet()),
                dto.getArticleCommentDtos().stream()
                        .map(ArticleCommentResponse::from)
                        .collect(Collectors.toSet())
        );
    }

}
