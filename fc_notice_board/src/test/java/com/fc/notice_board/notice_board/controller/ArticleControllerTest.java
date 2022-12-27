package com.fc.notice_board.notice_board.controller;

import com.fc.notice_board.notice_board.config.SpringSecurityConfig;
import com.fc.notice_board.notice_board.config.TestSecurityConfig;
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
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
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
@Import({TestSecurityConfig.class, FormDataEncoder.class})
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

    @WithMockUser
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

    @WithMockUser // 스프링 시큐리티에서 인증이 완료된 상태로 테스트 진행
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

    // 스프링 시큐리티에서 UserDetailsService 에서 실제 존재하는 유저가 있을때 인증 완료.
    // 테스트에서는 TestSecurityConfig 를 통해 스프링 시큐리티를 인증하고, serAccountRepository.findById() 호출시 항상 userId 가 "kmsTest"인 유저를 반환하도록 설정
    // 그러므로, value 를 "kmsTest" 로 두면 유저가 있다록 가정하고 인증이 된 상태로 테스트 진행됨
    @WithUserDetails(value = "kmsTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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

    @WithMockUser
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

    @WithUserDetails(value = "ksmTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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

    // 스프링 시큐리티에서 UserDetailsService 에서 실제 존재하는 유저가 있을때 인증 완료.
    // 테스트에서는 TestSecurityConfig 를 통해 스프링 시큐리티를 인증하고, serAccountRepository.findById() 호출시 항상 userId 가 "kmsTest"인 유저를 반환하도록 설정
    // 그러므로, value 를 "kmsTest" 로 두면 유저가 있다록 가정하고 인증이 된 상태로 테스트 진행됨
    @WithUserDetails(value = "kmsTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Delete Article - Success")
    @Test
    void givenArticleIdToDelete_whenRequesting_thenDeletesArticle() throws Exception {
        // Given
        long articleId = 1L;
        String userId = "kmsTest";
        willDoNothing().given(articleService).deleteArticle(articleId, userId);

        // When & Then
        mvc.perform(post("/articles/" + articleId + "/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));

        then(articleService).should().deleteArticle(articleId, userId);
    }

    @DisplayName("[view][GET] Requesting Article Page w/o Logging In - Redirects to Login Page")
    @Test
    void givenNothing_whenRequesting_thenRedirectToLoginPage() throws Exception {
        // Given
        long articleId = 1L;

        // When & Then
        mvc.perform(get("/articles/" + articleId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        then(articleService).shouldHaveNoInteractions();
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
                "kmsTest",
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