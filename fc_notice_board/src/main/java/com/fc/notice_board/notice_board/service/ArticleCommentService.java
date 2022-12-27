package com.fc.notice_board.notice_board.service;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.ArticleComment;
import com.fc.notice_board.notice_board.domain.UserAccount;
import com.fc.notice_board.notice_board.dto.ArticleCommentDto;
import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.request.ArticleCommentRequest;
import com.fc.notice_board.notice_board.repository.ArticleCommentRepository;
import com.fc.notice_board.notice_board.repository.ArticleRepository;
import com.fc.notice_board.notice_board.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    public List<ArticleCommentDto> searchArticleComment(Long articleId) {
        List<ArticleComment> articleComments = articleCommentRepository.findByArticle_Id(articleId);

        return articleComments.stream()
                .map(ArticleCommentDto::from)
                .toList();
    }

    @Transactional
    public void saveArticleComment(ArticleCommentDto articleCommentDto) {
        Long articleId = articleCommentDto.getArticleId();
        String content = articleCommentDto.getContent();
        String userId = articleCommentDto.getUserAccountDto().getUserId();

        if (content == null || content.isBlank()) {
            return;
        }

        try {
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(userId);

            articleCommentRepository.save(
                    ArticleComment.of(article, content, LocalDateTime.now(), "kms", userAccount));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Entity Not Found.");
        }
    }

    @Transactional
    public void deleteArticleComment(Long articleCommentId, String userId) {
        articleCommentRepository.deleteByIdAndUserAccount_UserId(articleCommentId, userId);
    }
}
