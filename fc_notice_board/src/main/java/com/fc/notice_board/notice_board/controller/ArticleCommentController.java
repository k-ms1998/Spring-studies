package com.fc.notice_board.notice_board.controller;

import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.request.ArticleCommentRequest;
import com.fc.notice_board.notice_board.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/create")
    public String createNewArticleComment(ArticleCommentRequest articleCommentRequest) {
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(
                UserAccountDto.of("uno", "password", "uno@gmail.com", "Uno", "memo")));

        return "redirect:/articles/" + articleCommentRequest.getArticleId();
    }

    @PostMapping("/{articleCommentId}/delete")
    public String deleteNewArticleComment(@PathVariable Long articleCommentId, Long articleId) {
        articleCommentService.deleteArticleComment(articleCommentId);

        return "redirect:/articles/" + articleId;
    }
}
