package com.fc.notice_board.notice_board.dto.response;

import com.fc.notice_board.notice_board.dto.ArticleCommentDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"id", "parentCommentId", "content", "createdAt"})
public class ArticleCommentResponse {

    private Long id;
    private String content;
    private String email;
    private String nickname;
    private String userId;
    private Long parentCommentId;
    private Set<ArticleCommentResponse> childComments;
    private LocalDateTime createdAt;

    public static ArticleCommentResponse of(Long id, String content, String email, String nickname, String userId, LocalDateTime createdAt) {
        return new ArticleCommentResponse(id, content, email, nickname, userId, null, null, createdAt);
    }

    public static ArticleCommentResponse of(Long id, String content, String email, String nickname, String userId, Long parentCommentId, LocalDateTime createdAt) {
        /*
        ArticleCommentResponse 를 정렬할 규칙
         */
        Comparator<ArticleCommentResponse> childCommentComparator = Comparator.comparing(ArticleCommentResponse::getCreatedAt)
                .thenComparingLong(ArticleCommentResponse::getParentCommentId);
        return new ArticleCommentResponse(id, content, email, nickname, userId,
                parentCommentId, new TreeSet<>(childCommentComparator), createdAt
        ); //new TreeSet<>(childCommentComparator) -> childCommentComparator 에 의해 정렬된 자식 댓글들
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        String nickname = dto.getUserAccountDto().getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.getUserAccountDto().getUserId();
        }

        return ArticleCommentResponse.of(
                dto.getId(),
                dto.getContent(),
                dto.getUserAccountDto().getEmail(),
                nickname,
                dto.getUserAccountDto().getUserId(),
                dto.getParentCommentId(),
                dto.getCreatedAt()
        );
    }

    public boolean hasParentComment() {
        return this.getParentCommentId() != null;
    }
}
