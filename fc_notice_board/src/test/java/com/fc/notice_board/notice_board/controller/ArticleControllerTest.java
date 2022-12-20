package com.fc.notice_board.notice_board.controller;

import com.fc.notice_board.notice_board.config.SpringSecurityConfig;
import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.UserAccount;
import com.fc.notice_board.notice_board.dto.ArticleDto;
import com.fc.notice_board.notice_board.dto.ArticleWithCommentsDto;
import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.enums.FormStatus;
import com.fc.notice_board.notice_board.dto.enums.SearchType;
import com.fc.notice_board.notice_board.dto.request.ArticleRequest;
import com.fc.notice_board.notice_board.dto.response.ArticleResponse;
import com.fc.notice_board.notice_board.repository.ArticleRepository;
import com.fc.notice_board.notice_board.service.ArticleService;
import com.fc.notice_board.notice_board.service.PaginationService;
import com.fc.notice_board.notice_board.util.FormDataEncoder;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View Controller - Articles")
@WebMvcTest(ArticleController.class)
@Import({SpringSecurityConfig.class, FormDataEncoder.class})
class ArticleControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private PaginationService paginationService;

    @MockBean
    private ArticleRepository articleRepository;

    public ArticleControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
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
        given(articleService.searchArticlesWithComments(articleId)).willReturn(createArticleWithCommentsDto());

        // When && Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));

        then(articleService).should().searchArticlesWithComments(articleId);
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

    @DisplayName("[view][GET] Render Create Article View")
    @Test
    void givenNothing_whenRequesting_thenReturnsNewArticlePage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/articles/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    @DisplayName("[view][POST] Create Article - Success")
    @Test
    void givenNewArticleInfo_whenRequesting_thenSavesNewArticle() throws Exception {
        // Given
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        // When & Then
        mvc.perform(
                        post("/articles/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().saveArticle(any(ArticleDto.class));
    }

    @DisplayName("[view][GET] Render Article Update View")
    @Test
    void givenNothing_whenRequesting_thenReturnsUpdatedArticlePage() throws Exception {
        // Given
        long articleId = 1L;
        ArticleDto dto = createArticleDto();
        given(articleService.getArticle(articleId)).willReturn(dto);

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        then(articleService).should().getArticle(articleId);
    }

    @DisplayName("[view][POST] Update Article - Success")
    @Test
    void givenUpdatedArticleInfo_whenRequesting_thenUpdatesArticle() throws Exception {
        // Given
        long articleId = 1L;
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");

        // When & Then
        mvc.perform(post("/articles/" + articleId + "/form")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(articleRequest))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));
    }

    @DisplayName("[view][POST] Delete Article - Success")
    @Test
    void givenArticleIdToDelete_whenRequesting_thenDeletesArticle() throws Exception {
        // Given
        long articleId = 1L;
        willDoNothing().given(articleService).deleteArticle(articleId);

        // When & Then
        mvc.perform(post("/articles/" + articleId + "/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));

        then(articleService).should().deleteArticle(articleId);
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

    private ArticleDto createArticleDto() {
        return ArticleDto.of(
                "title",
                "content",
                "#java",
                createUserAccountDto()
        );
    }
}