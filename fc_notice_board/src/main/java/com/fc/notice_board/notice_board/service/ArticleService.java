package com.fc.notice_board.notice_board.service;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.dto.ArticleDto;
import com.fc.notice_board.notice_board.dto.ArticleSearchParameters;
import com.fc.notice_board.notice_board.dto.ArticleWithCommentsDto;
import com.fc.notice_board.notice_board.dto.enums.SearchType;
import com.fc.notice_board.notice_board.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public Page<ArticleDto> searchArticles(SearchType type, String searchKeyword, Pageable pageable) {
        if(searchKeyword == null || searchKeyword.isBlank()){
            return articleRepository.findAll(pageable)
                    .map(ArticleDto::from);
        }

        return switch (type) {
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable);
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable);
            case HASHTAG -> articleRepository.findByHashtag(searchKeyword, pageable);
        };
    }

    public ArticleWithCommentsDto searchArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Article Not Found."));
    }

    @Transactional
    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    @Transactional
    public Article updateArticle(ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.getId());
            article.update(dto);
            return articleRepository.save(article);
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다. dto->{}", dto);
            throw new EntityNotFoundException("Article Not Found.");
        }
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        articleRepository.deleteById(articleId);
    }
}
