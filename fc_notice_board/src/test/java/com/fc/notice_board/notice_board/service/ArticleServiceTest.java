package com.fc.notice_board.notice_board.service;

import java.io.*;
import java.util.*;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.UserAccount;
import com.fc.notice_board.notice_board.dto.ArticleDto;
import com.fc.notice_board.notice_board.dto.ArticleSearchParameters;
import com.fc.notice_board.notice_board.dto.ArticleWithCommentsDto;
import com.fc.notice_board.notice_board.dto.UserAccountDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

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
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> result = sut.searchArticles(null, null, pageable); // 제목, 본문, ID, 닉네임, 해시테그로 검색 가능

        // Then
        Assertions.assertThat(result).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    /*
    검색(Title) + 페이지네이션
     */
    @Test
    @DisplayName("[Article][Service] Search Articles With Title - Returns Articles")
    void givenSearchParametersTitle_whenSearchingArticlesWithTitle_thenReturnsArticles() throws Exception {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        SearchType type = SearchType.TITLE;
        String title = "title";
        given(articleRepository.findByTitleContaining(title, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> result = sut.searchArticles(type, title, pageable); // 제목, 본문, ID, 닉네임, 해시테그로 검색 가능

        // Then
        Assertions.assertThat(result).isEmpty();
        then(articleRepository).should().findByTitleContaining(title, pageable);
    }

    /*
    각 게시글 페이지로 이동
     */
    @Test
    @DisplayName("[Article][Service] Click Article - Returns Article")
    void givenArticleId_whenClickingArticle_thenReturnsClickedArticle() throws Exception {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleWithCommentsDto result = sut.searchArticle(articleId); // 제목, 본문, ID, 닉네임, 해시테그로 검색 가능

        // Then
//        Assertions.assertThat(result)
//                .hasFieldOrPropertyWithValue("title", article.getTitle());
        then(articleRepository).should().findById(articleId);
    }

    @Test
    @DisplayName("[Article][Service] Saving Article")
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() throws Exception {
        // Given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // When
        sut.saveArticle(dto);

        // Then
        then(articleRepository).should().save(any(Article.class));

    }

    @Test
    @DisplayName("[Article][Service] Updating Article")
    @Transactional
    void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() throws Exception {
        // Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto();
//        articleRepository.save(article);
        given(articleRepository.getReferenceById(dto.getId())).willReturn(article);

        // When
        Article result = sut.updateArticle(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.getId());
    }

    @Test
    @DisplayName("[Article][Service] Deleting Article")
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() throws Exception {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        willDoNothing().given(articleRepository).delete(article);

        // When
        sut.deleteArticle(articleId);

        // Then
        then(articleRepository).should().delete(article);
    }

    @DisplayName("[Article][Service] - When searching via hashtags - returns list of unique hashtags [Passed]")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() throws Exception {
        // Given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        given(articleRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);

        // When
        List<String> actualHashtags = sut.getHashtags();
        System.out.println("actualHashtags = " + actualHashtags);

        // Then
        Assertions.assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(articleRepository).should().findAllDistinctHashtags();

    }

    public static Article createArticle() {
        return Article.of(1L, "title", "content", "#hashtag",
                createUserAccount());
    }

    public static ArticleDto createArticleDto() {
        return ArticleDto.of(1L, "titleDto", "contentDto", "#hashtagDto",
                createUserAccountDto());
    }

    public static UserAccount createUserAccount() {
        return UserAccount.of("userId", "userPassword", "email",
                "nickname", "memo", "createdBy", "modifiedBy");
    }

    public static UserAccountDto createUserAccountDto() {
        return UserAccountDto.from(createUserAccount());
    }
}