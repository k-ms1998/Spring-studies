package com.fc.notice_board.notice_board.service;

import java.io.*;
import java.util.*;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.dto.ArticleDto;
import com.fc.notice_board.notice_board.dto.ArticleSearchParameters;
import com.fc.notice_board.notice_board.dto.enums.SearchType;
import com.fc.notice_board.notice_board.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;
    @Mock private ArticleRepository articleRepository;


    /*
    검색 + 페이지네이션
     */
    @Test
    @DisplayName("[Article][Service] Search Articles - Returns Articles")
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticles() throws Exception {
        // Given
        ArticleSearchParameters searchParameters = ArticleSearchParameters.of(SearchType.TITLE, "search keyword");

        // When
        Page<ArticleDto> result = sut.searchArticles(searchParameters); // 제목, 본문, ID, 닉네임, 해시테그로 검색 가능

        // Then
        Assertions.assertThat(result).isNotNull();

    }

    /*
    각 게시글 페이지로 이동
     */
    @Test
    @DisplayName("[Article][Service] Click Article - Returns Article")
    void givenArticleId_whenClickingArticle_thenReturnsClickedArticle() throws Exception {
        // Given
        ArticleSearchParameters searchParameters = ArticleSearchParameters.of(SearchType.ID, "search keyword");

        // When
        ArticleDto result = sut.searchArticle(searchParameters); // 제목, 본문, ID, 닉네임, 해시테그로 검색 가능

        // Then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("[Article][Service] Saving Article")
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() throws Exception {
        // Given
        ArticleDto dto = ArticleDto.of("title1", "content1", "hashtag1");
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // When
        sut.saveArticle(dto);

        // Then
        then(articleRepository).should().save(any(Article.class));

    }

    @Test
    @DisplayName("[Article][Service] Updating Article")
    void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() throws Exception {
        // Given
        Long articleId = 1L;
        ArticleDto dto = ArticleDto.of("title1", "content1", "hashtag1");
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // When
        sut.updateArticle(articleId, dto);

        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @Test
    @DisplayName("[Article][Service] Deleting Article")
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() throws Exception {
        // Given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).delete(any(Article.class));

        // When
        sut.deleteArticle(articleId);

        // Then
        then(articleRepository).should().delete(any(Article.class));
    }
}