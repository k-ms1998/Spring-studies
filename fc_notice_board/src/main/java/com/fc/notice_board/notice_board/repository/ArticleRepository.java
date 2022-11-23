package com.fc.notice_board.notice_board.repository;

import com.fc.notice_board.notice_board.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
