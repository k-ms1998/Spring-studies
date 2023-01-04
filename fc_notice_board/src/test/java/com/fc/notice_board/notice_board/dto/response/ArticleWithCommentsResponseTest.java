package com.fc.notice_board.notice_board.dto.response;

import com.fc.notice_board.notice_board.dto.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Article With Comments Response Test")
class ArticleWithCommentsResponseTest {


    @DisplayName("[ArticleWithCommentsResponse] Given DTO w/o child comments - Returns Organized Comments")
    @Test
    void givenArticleWiltCommentsDtoWithoutChildComments_whenMapping_thenReturnsOrganizedComments() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<ArticleCommentDto> articleCommentDtos = Set.of(
                createArticleCommentDto(1L, null, now),
                createArticleCommentDto(2L, null, now.minusDays(1L)),
                createArticleCommentDto(3L, null, now.plusDays(2L)),
                createArticleCommentDto(4L, null, now.plusDays(1L))
        );
        ArticleWithCommentsDto articleWithCommentsDto = createAriArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse response = ArticleWithCommentsResponse.from(articleWithCommentsDto);

        // Then
        Assertions.assertThat(response.getArticleCommentsResponse())
                .containsExactly(
                        createArticleCommentResponse(3L, null, now.plusDays(2L)),
                        createArticleCommentResponse(4L, null, now.plusDays(1L)),
                        createArticleCommentResponse(1L, null, now),
                        createArticleCommentResponse(2L, null, now.minusDays(1L))
                );
    }

    @DisplayName("[ArticleWithCommentsResponse] Given DTO w/ child comments - Returns Organized Comments")
    @Test
    void givenArticleWiltCommentsDtoWithChildComments_whenMapping_thenReturnsOrganizedComments() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<ArticleCommentDto> articleCommentDtos = Set.of(
                createArticleCommentDto(1L, 5L, now),
                createArticleCommentDto(2L, 6L, now.minusDays(1L)),
                createArticleCommentDto(3L, 5L, now.plusDays(2L)),
                createArticleCommentDto(4L, 6L, now.plusDays(1L)),
                createArticleCommentDto(5L, null, now.plusDays(1L)),
                createArticleCommentDto(6L, null, now.plusDays(3L))
        );
        ArticleWithCommentsDto articleWithCommentsDto = createAriArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse response = ArticleWithCommentsResponse.from(articleWithCommentsDto);
        Set<ArticleCommentResponse> childComments = new LinkedHashSet<>();
        response.getArticleCommentsResponse().forEach(articleCommentResponse -> {
            childComments.addAll(articleCommentResponse.getChildComments());
        });
        System.out.println("childComments = " + childComments);

        // Then
        Assertions.assertThat(response.getArticleCommentsResponse())
                .containsExactly(
                        createArticleCommentResponse(6L, null, now.plusDays(3L)),
                        createArticleCommentResponse(5L, null, now.plusDays(1L))
                );
//        Assertions.assertThat(childComments)
//                .containsExactly(
//                        createArticleCommentResponse(2L, 6L, now.minusDays(1L)),
//                        createArticleCommentResponse(4L, 6L, now.plusDays(1L)),
//                        createArticleCommentResponse(1L, 5L, now),
//                        createArticleCommentResponse(3L, 5L, now.plusDays(2L))
//                );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "kms",
                "password",
                "test@email.com",
                "nickname",
                "memo"
        );
    }

    private ArticleWithCommentsDto createAriArticleWithCommentsDto(Set<ArticleCommentDto> articleCommentDtos) {
        return ArticleWithCommentsDto.of(
                createUserAccountDto(),
                createArticleDto(),
                articleCommentDtos
        );
    }

    private ArticleCommentDto createArticleCommentDto(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return ArticleCommentDto.of(
                id,
                1L,
                parentCommentId,
                createUserAccountDto(),
                "test comment: " + id,
                createdAt,
                "kms"
        );
    }

    private ArticleCommentResponse createArticleCommentResponse(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return ArticleCommentResponse.of(
                id,
                "test comment: " + id,
                "test@email.com",
                "nickname",
                "kms",
                parentCommentId,
                createdAt
        );
    }

    private ArticleDto createArticleDto() {
        return ArticleDto.of(
                1L,
                "title",
                "cotent",
                Set.of(HashtagDto.of("java")),
                createUserAccountDto()
        );
    }
}