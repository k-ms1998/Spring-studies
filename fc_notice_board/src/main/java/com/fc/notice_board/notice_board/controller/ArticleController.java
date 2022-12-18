package com.fc.notice_board.notice_board.controller;

import com.fc.notice_board.notice_board.dto.enums.SearchType;
import com.fc.notice_board.notice_board.dto.response.ArticleResponse;
import com.fc.notice_board.notice_board.dto.response.ArticleWithCommentsResponse;
import com.fc.notice_board.notice_board.service.ArticleService;
import com.fc.notice_board.notice_board.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        ArticleWithCommentsResponse response = ArticleWithCommentsResponse.from(articleService.searchArticle(articleId));
        model.addAttribute("article", response);
        model.addAttribute("articleComments", response.getArticleCommentsResponse());

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
}
