package com.fc.notice_board.notice_board.controller;

import com.fc.notice_board.notice_board.config.SpringSecurityConfig;
import com.fc.notice_board.notice_board.domain.UserAccount;
import com.fc.notice_board.notice_board.dto.ArticleDto;
import com.fc.notice_board.notice_board.dto.ArticleWithCommentsDto;
import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.enums.SearchType;
import com.fc.notice_board.notice_board.repository.ArticleRepository;
import com.fc.notice_board.notice_board.service.ArticleService;
import com.fc.notice_board.notice_board.service.PaginationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View Controller - Articles")
@WebMvcTest(ArticleController.class)
@Import(SpringSecurityConfig.class)
class ArticleControllerTest {

    private final MockMvc mvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private PaginationService paginationService;

    @MockBean
    private ArticleRepository articleRepository;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] - Get All Articles [Passed]")
    @Test
    void givenNoting_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class)))
                .willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When && Then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("searchTypes"))
                .andExpect(model().attributeExists("paginationBarNumbers"));

        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] - Get Specific Articles with Comments[Passed]")
    @Test
    void givenNoting_whenRequestingSpecificArticleView_thenReturnsSpecificArticleView() throws Exception {
        // Given
        Long articleId = 1L;
        given(articleService.searchArticle(articleId)).willReturn(createArticleWithCommentsDto());

        // When && Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));

        then(articleService).should().searchArticle(articleId);
    }

    @DisplayName("[view][GET] - Search Articles by Title [Passed]")
    @Test
    void givenTitleKeyword_whenSearchingArticlesView_thenReturnsView() throws Exception {
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        given(articleService.searchArticles(eq(searchType), eq(searchValue), any(Pageable.class)))
                .willReturn(Page.empty());
        given(paginationService
                .getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When && Then
        mvc.perform(get("/articles")
                        .queryParam("searchType", searchType.name())
                        .queryParam("searchValue", searchValue))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("searchTypes"));

        then(articleService).should().searchArticles(eq(searchType), eq(searchValue), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());

    }

    @DisplayName("[view][GET] - Given no search parameters when searching via hashtag - return empty page [Passed]")
    @Test
    void givenNoSearchParameters_whenSearchingArticleViaHashtag_thenReturnsEmptyPage() throws Exception {
        // Given
        given(articleService.searchArticlesViaHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());

        // When &Then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));

        then(articleService).should().searchArticlesViaHashtag(eq(null), any(Pageable.class)); // 검색 키워드가 없으면 Service Layer 에서 오류를 처리하고 Repository 까지 갈 필요가 없으므로 articleRepository 까지 호출될 필요 없음
    }

    @DisplayName("[view][GET] - Given search parameters when searching via hashtag - return page [Passed]")
    @Test
    void givenHashtag_whenSearchingArticleViaHashtag_thenReturnsPage() throws Exception {
        // Given
        String hashtag = "#java";
        given(articleService.searchArticlesViaHashtag(eq(hashtag), any(Pageable.class))).willReturn(Page.empty());

        // When && Then
        mvc.perform(get("/articles/search-hashtag")
                        .queryParam("searchValue", hashtag))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));

        then(articleService).should().searchArticlesViaHashtag(eq(hashtag), any(Pageable.class));
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        UserAccountDto userAccountDto = createUserAccountDto();

        return new ArticleWithCommentsDto(
                1L,
                userAccountDto,
                ArticleDto.of(1L, "title", "content", "#hashtag", userAccountDto),
                Set.of()
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "userId",
                "userPassword",
                "email",
                "nickname",
                "memo",
                "Seop",
                "Seop");
    }
}