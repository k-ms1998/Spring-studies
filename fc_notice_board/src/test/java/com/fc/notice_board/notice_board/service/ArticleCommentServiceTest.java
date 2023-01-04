package com.fc.notice_board.notice_board.service;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.ArticleComment;
import com.fc.notice_board.notice_board.domain.UserAccount;
import com.fc.notice_board.notice_board.dto.ArticleCommentDto;
import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.repository.ArticleCommentRepository;
import com.fc.notice_board.notice_board.repository.ArticleRepository;
import com.fc.notice_board.notice_board.repository.UserAccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks private ArticleCommentService sut;
    @Mock private ArticleCommentRepository articleCommentRepository;
    @Mock private ArticleRepository articleRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @Test
    @DisplayName("[ArticleComment][Service] Searching Article Comments with Article ID - Returns Article Comments")
    void givenArticleId_whenSearchingArticleComments_thenReturnsArticleComments() throws Exception {
        // Given
        Long articleId = 1L;
        ArticleComment expectedParentComment = createArticleComment(1L, "parent content");
        ArticleComment expectedChildComment = createArticleComment(2L, "child content");
        expectedChildComment.setParentCommentId(expectedParentComment.getId());
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(expectedParentComment, expectedChildComment));

        // When
        List<ArticleCommentDto> result = sut.searchArticleComment(articleId);

        // Then
        Assertions.assertThat(result).hasSize(2);
        then(articleCommentRepository).should().findByArticle_Id(articleId);
    }

    @Test
    @DisplayName("[ArticleComment][Service] Creating Article Comments")
    void givenArticleId_whenCreatingArticleComment_thenReturnsArticleComment() throws Exception {
        // Given
        ArticleCommentDto comment = createArticleCommentDto(null, "comment");
        given(articleRepository.getReferenceById(comment.getArticleId())).willReturn(createArticle());
        given(userAccountRepository.getReferenceById(comment.getUserAccountDto().getUserId())).willReturn(createUserAccount());

        // When
        sut.saveArticleComment(comment);

        // Then
        then(articleRepository).should().getReferenceById(comment.getArticleId());
        then(userAccountRepository).should().getReferenceById(comment.getUserAccountDto().getUserId());
    }

    @Test
    @DisplayName("[ArticleComment][Service] Deleting Article Comments")
    void givenArticleId_whenDeletingArticleComment_thenReturnsView() throws Exception {
        // Given
        Long articleCommentId = 1L;
        String userId = "kmsTest";
        willDoNothing().given(articleCommentRepository).deleteByIdAndUserAccount_UserId(eq(articleCommentId), eq(userId));

        // When
        sut.deleteArticleComment(articleCommentId, userId);

        // Then
        then(articleCommentRepository).should().deleteByIdAndUserAccount_UserId(eq(articleCommentId), eq(userId));
    }

    @Test
    @DisplayName("[ArticleComment][Service] Create Child Comment")
    void givenParentCommentIdAndArticleCommentInfo_whenCreating_thenSavesChildComment() throws Exception {
        // Given
        Long parentCommentId = 1L;
        ArticleComment parentComment = createArticleComment(parentCommentId, "Parent Comment");
        ArticleCommentDto childComment = createArticleCommentDto(parentCommentId, "Child Comment");
        given(articleRepository.getReferenceById(childComment.getArticleId())).willReturn(createArticle());
        given(userAccountRepository.getReferenceById(childComment.getUserAccountDto().getUserId())).willReturn(createUserAccount());
        given(articleCommentRepository.getReferenceById(childComment.getParentCommentId())).willReturn(parentComment);

        // When
        sut.saveArticleComment(childComment);

        // Then
        Assertions.assertThat(childComment.getParentCommentId()).isNotNull();
        then(articleRepository).should().getReferenceById(childComment.getArticleId());
        then(userAccountRepository).should().getReferenceById(childComment.getUserAccountDto().getUserId());
        then(articleCommentRepository).should().getReferenceById(childComment.getParentCommentId());
    }

    private ArticleCommentDto createArticleCommentDto(Long parentCommentId, String content) {
        return ArticleCommentDto.of(
                1L,
                parentCommentId,
                UserAccountDto.from(createUserAccount()),
                content
        );
    }

    private ArticleComment createArticleComment(Long parentCommentId, String content) {
        return ArticleComment.of(
                createArticle(),
                content,
                LocalDateTime.now(),
                "kms",
                createUserAccount(),
                parentCommentId
        );
    }

    private Article createArticle() {
        return Article.of(
                1L,
                "title",
                "content",
                createUserAccount()
        );
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "user",
                "password",
                "email@email.com",
                "nickname",
                "memo",
                "user",
                "user"
        );
    }

}