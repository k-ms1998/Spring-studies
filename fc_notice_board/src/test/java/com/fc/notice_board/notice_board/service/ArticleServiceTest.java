package com.fc.notice_board.notice_board.service;

import java.util.*;
import java.util.stream.Collectors;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.Hashtag;
import com.fc.notice_board.notice_board.domain.UserAccount;
import com.fc.notice_board.notice_board.dto.ArticleDto;
import com.fc.notice_board.notice_board.dto.ArticleWithCommentsDto;
import com.fc.notice_board.notice_board.dto.HashtagDto;
import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.enums.SearchType;
import com.fc.notice_board.notice_board.repository.ArticleRepository;
import com.fc.notice_board.notice_board.repository.HashtagRepository;
import com.fc.notice_board.notice_board.repository.UserAccountRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;
    @Mock private ArticleRepository articleRepository;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private HashtagService hashtagService;
    @Mock private HashtagRepository hashtagRepository;

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
        ArticleWithCommentsDto result = sut.searchArticlesWithComments(articleId); // 제목, 본문, ID, 닉네임, 해시테그로 검색 가능

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
    void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용 #springBoot");
        Set<String> expectedHashtagNames = Set.of("springBoot");
        Set<Hashtag> expectedHashtags = new HashSet<>();

        given(articleRepository.getReferenceById(dto.getId())).willReturn(article);
        given(userAccountRepository.getReferenceById(dto.getUserAccountDto().getUserId())).willReturn(dto.getUserAccountDto().toEntity());
        willDoNothing().given(articleRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutArticles(any());
        given(hashtagService.parseHashtagNames(dto.getContent())).willReturn(expectedHashtagNames);
        given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);

        // When
        sut.updateArticle(dto.getId(), dto);

        // Then
        Assertions.assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.getTitle())
                .hasFieldOrPropertyWithValue("content", dto.getContent())
                .extracting("hashtags", as(InstanceOfAssertFactories.COLLECTION))
                .hasSize(1)
                .extracting("hashtagName")
                .containsExactly("springBoot");
        then(articleRepository).should().getReferenceById(dto.getId());
        then(userAccountRepository).should().getReferenceById(dto.getUserAccountDto().getUserId());
        then(articleRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutArticles(any());
        then(hashtagService).should().parseHashtagNames(dto.getContent());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);

    }

    @Test
    @DisplayName("[Article][Service] Deleting Article")
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() throws Exception {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        String userId = "kmsTest";
        given(articleRepository.getReferenceById(articleId)).willReturn(article);
        willDoNothing().given(articleRepository).deleteByIdAndUserAccount_UserId(articleId, userId);

        // When
        sut.deleteArticle(articleId, userId);

        // Then
        then(articleRepository).should().deleteByIdAndUserAccount_UserId(articleId, userId);
        then(articleRepository).should().getReferenceById(articleId);
    }

    @DisplayName("[Article][Service] - When searching via hashtags - returns list of unique hashtags [Passed]")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() throws Exception {
        // Given
        List<String> expectedHashtags = List.of("java", "spring", "boot");
        given(hashtagRepository.findAllHashtagNames()).willReturn(expectedHashtags);

        // When
        List<String> actualHashtags = sut.getHashtags();
        System.out.println("actualHashtags = " + actualHashtags);

        // Then
        Assertions.assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(hashtagRepository).should().findAllHashtagNames();

    }

    @DisplayName("[Article][Service] Given Article Id - Return Article With Comments")
    @Test
    void givenArticleId_whenSearchingArticleWithComments_thenReturnsArticleWithComments() {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleWithCommentsDto dto = sut.searchArticlesWithComments(articleId);

        // Then
        Assertions.assertThat(dto.getArticleDto())
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("[Article][Service] Given Invalid Article Id - Throws Exception")
    @Test
    void givenInvalidArticleId_whenSearchingArticleWithComments_thenThrowsException() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.searchArticlesWithComments(articleId));

        // Then
        Assertions.assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Article Not Found.");

        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("Given None Existent Hashtag - Returns Empty Page")
    @Test
    void givenNonexistentHashtag_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() {
        // Given
        String hashtagName = "None-Existent";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(new PageImpl<>(List.of(), pageable, 0));

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtagName, pageable);

        // Then
        Assertions.assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    public Article createArticle() {
        Article article =  Article.of(1L, "title", "content", createUserAccount());

        article.addHashtags(Set.of(
                createHashtag(1L, "java"),
                createHashtag(2L, "spring")
        ));

        return article;
    }

    public static ArticleDto createArticleDto() {
        return ArticleDto.of(1L, "titleDto", "contentDto", Set.of(HashtagDto.of("java")),
                createUserAccountDto());
    }

    public static ArticleDto createArticleDto(String title, String content) {
        return ArticleDto.of(1L, title, content, Set.of(HashtagDto.of("java")),
                createUserAccountDto());
    }

    public static UserAccount createUserAccount() {
        return UserAccount.of("userId", "userPassword", "email",
                "nickname", "memo", "createdBy", "modifiedBy");
    }

    public static UserAccountDto createUserAccountDto() {
        return UserAccountDto.from(createUserAccount());
    }

    private Hashtag createHashtag(String hashtagName) {
        return createHashtag(1L, hashtagName);
    }

    private Hashtag createHashtag(Long id, String hashtagName) {
        Hashtag hashtag = Hashtag.of(hashtagName);
        ReflectionTestUtils.setField(hashtag, "id", id);

        return hashtag;
    }

    private HashtagDto createHashtagDto() {
        return HashtagDto.of("java");
    }

}