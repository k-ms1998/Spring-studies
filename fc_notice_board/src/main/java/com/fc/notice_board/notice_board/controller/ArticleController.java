package com.fc.notice_board.notice_board.controller;

import com.fc.notice_board.notice_board.dto.enums.SearchType;
import com.fc.notice_board.notice_board.dto.response.ArticleResponse;
import com.fc.notice_board.notice_board.dto.response.ArticleWithCommentsResponse;
import com.fc.notice_board.notice_board.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public String getArticles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap model) {

        model.addAttribute("articles",
                articleService.searchArticles(searchType, searchValue, pageable)
                        .map(ArticleResponse::from));
        model.addAttribute("searchTypes", SearchType.values());

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String getArticle(@PathVariable Long articleId, ModelMap model) {
        ArticleWithCommentsResponse response = ArticleWithCommentsResponse.from(articleService.searchArticle(articleId));
        model.addAttribute("article", response);
        model.addAttribute("articleComments", response.getArticleCommentsResponse());

        return "articles/detail";
    }
}
