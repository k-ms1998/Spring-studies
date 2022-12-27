package com.fc.notice_board.notice_board.controller;

import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.request.ArticleCommentRequest;
import com.fc.notice_board.notice_board.dto.security.BoardPrincipal;
import com.fc.notice_board.notice_board.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/create")
    public String createNewArticleComment(@AuthenticationPrincipal BoardPrincipal boardPrincipal,
                                          ArticleCommentRequest articleCommentRequest) {
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/" + articleCommentRequest.getArticleId();
    }

    @PostMapping("/{articleCommentId}/delete")
    public String deleteNewArticleComment(@PathVariable Long articleCommentId,
                                          @AuthenticationPrincipal BoardPrincipal boardPrincipal,
                                          Long articleId) {
        articleCommentService.deleteArticleComment(articleCommentId, boardPrincipal.getUsername());

        return "redirect:/articles/" + articleId;
    }
}
