package com.fc.notice_board.notice_board.service;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.dto.ArticleCommentDto;
import com.fc.notice_board.notice_board.repository.ArticleCommentRepository;
import com.fc.notice_board.notice_board.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks private ArticleCommentService sut;
    @Mock private ArticleCommentRepository articleCommentRepository;
    @Mock private ArticleRepository articleRepository;

    @Test
    @DisplayName("[ArticleComment][Service] Searching Article Comments with Article ID - Returns Article Comments")
    void givenArticleId_whenSearchingArticleComments_thenReturnsArticleComments() throws Exception {
        // Given
        Long articleId = 1L;
        Article article = Article.of("title", "content", "#hashtag");
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        List<ArticleCommentDto> result = sut.searchArticleComment(articleId);

        // Then
        Assertions.assertThat(result).isNotNull();
        then(articleRepository).should().findById(articleId);
    }

    @Test
    @DisplayName("[ArticleComment][Service] Creating Article Comments")
    void givenArticleId_whenCreatingArticleComment_thenReturnsArticleComment() throws Exception {
        // Given
        Long articleId = 1L;
        Article article = Article.of("title", "content", "#hashtag");
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        List<ArticleCommentDto> result = sut.searchArticleComment(articleId);

        // Then
        Assertions.assertThat(result).isNotNull();
        then(articleRepository).should().findById(articleId);
    }

}