package com.fc.notice_board.notice_board.controller;

import com.fc.notice_board.notice_board.config.SpringSecurityConfig;
import com.fc.notice_board.notice_board.config.TestSecurityConfig;
import com.fc.notice_board.notice_board.dto.ArticleCommentDto;
import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.request.ArticleCommentRequest;
import com.fc.notice_board.notice_board.service.ArticleCommentService;
import com.fc.notice_board.notice_board.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View Controller - Article Comments")
@WebMvcTest(ArticleCommentController.class)
@Import({TestSecurityConfig.class, FormDataEncoder.class})
class ArticleCommentControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private ArticleCommentService articleCommentService;

    public ArticleCommentControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @WithUserDetails(value = "kmsTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Create Article Comment - Success")
    @Test
    void givenParameters_whenCreatingArticleComment_thenReturnsView() throws Exception {
        // Given
        Long articleId = 1L;
        String content = "comment";
        ArticleCommentRequest articleCommentRequest = ArticleCommentRequest.of(articleId, content);
        willDoNothing().given(articleCommentService).saveArticleComment(any(ArticleCommentDto.class));

        // When & Then
        mvc.perform(post("/comments/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(articleCommentRequest))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId));

        then(articleCommentService).should().saveArticleComment(any(ArticleCommentDto.class));
    }

    @WithUserDetails(value = "kmsTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Delete Article Comment - Success")
    @Test
    void givenParameters_whenDeletingArticleComment_thenReturnsView() throws Exception {
        // Given
        Long articleId = 1L;
        Long articleCommentId = 1L;
        String content = "comment";
        String userId = "kmsTest";
        willDoNothing().given(articleCommentService).deleteArticleComment(articleCommentId, userId);

        // When & Then
        mvc.perform(post("/comments/" + articleCommentId + "/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId));

        then(articleCommentService).should().deleteArticleComment(articleCommentId, userId);
    }

    @WithUserDetails(value = "kmsTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Create Child Comment - Success")
    @Test
    void givenParameters_whenCreatingChildComment_thenReturnsView() throws Exception {
        // Given
        Long articleId = 1L;
        Long parentCommentId = 1L;
        ArticleCommentRequest articleCommentRequest = ArticleCommentRequest.of(articleId, parentCommentId, "content");
        String userId = "kmsTest";
        willDoNothing().given(articleCommentService).saveArticleComment(any(ArticleCommentDto.class));

        // When & Then
        mvc.perform(post("/comments/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(articleCommentRequest))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId));

        then(articleCommentService).should().saveArticleComment(any(ArticleCommentDto.class));
    }

    @WithUserDetails(value = "kmsTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] Delete Child Comment - Success")
    @Test
    void givenParameters_whenDeletingChildComment_thenReturnsView() throws Exception {
        // Given
        Long articleId = 1L;
        Long articleCommentId = 1L;
        String userId = "kmsTest";
        willDoNothing().given(articleCommentService).deleteArticleComment(articleCommentId, userId);

        // When & Then
        mvc.perform(post("/comments/" + articleCommentId + "/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId));

        then(articleCommentService).should().deleteArticleComment(articleCommentId, userId);
    }
}