package com.fc.notice_board.notice_board.controller;

import com.fc.notice_board.notice_board.dto.UserAccountDto;
import com.fc.notice_board.notice_board.dto.enums.SearchType;
import com.fc.notice_board.notice_board.dto.enums.FormStatus;
import com.fc.notice_board.notice_board.dto.request.ArticleRequest;
import com.fc.notice_board.notice_board.dto.response.ArticleResponse;
import com.fc.notice_board.notice_board.dto.response.ArticleWithCommentsResponse;
import com.fc.notice_board.notice_board.dto.security.BoardPrincipal;
import com.fc.notice_board.notice_board.service.ArticleService;
import com.fc.notice_board.notice_board.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping
    public String getArticles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap model) {

        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        model.addAttribute("articles", articles);
        model.addAttribute("searchTypes", SearchType.values());
        model.addAttribute("paginationBarNumbers", barNumbers);

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String getArticle(@PathVariable Long articleId, ModelMap model) {
        ArticleWithCommentsResponse response = ArticleWithCommentsResponse.from(articleService.searchArticlesWithComments(articleId));

        model.addAttribute("article", response);
        model.addAttribute("articleComments", response.getArticleCommentsResponse());
        model.addAttribute("totalCount", articleService.getArticleCount());

        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchArticlesViaHashtag(
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap model
    ) {
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<String> hashtags = articleService.getHashtags();
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());


        model.addAttribute("articles", articles);
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("searchType", SearchType.HASHTAG);
        model.addAttribute("paginationBarNumbers", barNumbers);

        return "articles/search-hashtag";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }

    @PostMapping ("/form")
    public String postNewArticle(ArticleRequest articleRequest,
                                 @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        articleService.saveArticle(articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId,
                                @AuthenticationPrincipal BoardPrincipal boardPrincipal,
                                ArticleRequest articleRequest) {
        articleService.updateArticle(articleId, articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/" + articleId;
    }

    @PostMapping ("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId,
                                @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        articleService.deleteArticle(articleId, boardPrincipal.getUsername());

        return "redirect:/articles";
    }
}
