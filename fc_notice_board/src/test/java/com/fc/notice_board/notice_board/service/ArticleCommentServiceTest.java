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
        ArticleComment expected = createArticleComment("content");
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(expected));

        // When
        List<ArticleCommentDto> result = sut.searchArticleComment(articleId);

        // Then
        Assertions.assertThat(result).hasSize(1);
        then(articleCommentRepository).should().findByArticle_Id(articleId);
    }

    @Test
    @DisplayName("[ArticleComment][Service] Creating Article Comments")
    void givenArticleId_whenCreatingArticleComment_thenReturnsArticleComment() throws Exception {
        // Given
        ArticleCommentDto comment = createArticleCommentDto("comment");
        given(articleRepository.getReferenceById(comment.getArticleId())).willReturn(createArticle());
        given(userAccountRepository.getReferenceById(comment.getUserAccountDto().getUserId())).willReturn(createUserAccount());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        // When
        sut.saveArticleComment(comment);

        // Then
        then(articleRepository).should().getReferenceById(comment.getArticleId());
        then(userAccountRepository).should().getReferenceById(comment.getUserAccountDto().getUserId());
        then(articleCommentRepository).should().save(any(ArticleComment.class));
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

    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
                1L,
                UserAccountDto.from(createUserAccount()),
                content
        );
    }

    private ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
                createArticle(),
                content,
                LocalDateTime.now(),
                "kms",
                createUserAccount()
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