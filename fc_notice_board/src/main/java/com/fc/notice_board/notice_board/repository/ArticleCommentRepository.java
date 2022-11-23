package com.fc.notice_board.notice_board.repository;

import com.fc.notice_board.notice_board.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
}
