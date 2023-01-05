package com.fc.notice_board.notice_board.dto.response;

import com.fc.notice_board.notice_board.domain.Hashtag;
import com.fc.notice_board.notice_board.dto.ArticleCommentDto;
import com.fc.notice_board.notice_board.dto.ArticleWithCommentsDto;
import com.fc.notice_board.notice_board.dto.HashtagDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
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
    Set<Hashtag> hashtags;
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
                organizeChildComments(dto.getArticleCommentDtos())
        );
    }

    private static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> dtos) {
        /*
        dto -> ArticleCommentResponse 로 변환
        변환 후, <K, V> = <자신의 id 값, 자기 자신> 으로 변환해서 저장
         */
        Map<Long, ArticleCommentResponse> map = dtos.stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toMap(ArticleCommentResponse::getId, Function.identity()));

        /*
        부모 댓글이 존재 하면, 부모 댓글에 자기 자신을 추가
        위에서,  <K, V> = <자신의 id 값, 자기 자신> 으로 변환해서 저장했기 때문에,
        부모 댓글의 id 값만 알고 있으면 바로 부모 댓글을 가져올 수 있음
         */
        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment) // 부모 댓글이 존재 할때
                .forEach(childComment -> {
                    ArticleCommentResponse parentComment = map.get(childComment.getParentCommentId()); // 부보 댓글 id로 부모 댓글 가져오기
                    parentComment.getChildComments().add(childComment); // 부모 댓글에 자기 자신 추가
                });

        return map.values().stream()
                .filter(commentResponse -> !commentResponse.hasParentComment())
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentResponse::getCreatedAt)
                                .reversed()
                                .thenComparingLong(ArticleCommentResponse::getId))
                ));
    }

}
