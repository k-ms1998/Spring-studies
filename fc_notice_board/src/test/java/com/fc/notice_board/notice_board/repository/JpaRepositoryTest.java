package com.fc.notice_board.notice_board.repository;

import com.fc.notice_board.notice_board.config.JpaConfig;
import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.ArticleComment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("JPA 연결 테스트")
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @Test
    @DisplayName("SELECT")
    void givenTestData_whenSelecting_thenSuccess() throws Exception {
        // Given

        // When
        List<Article> allArticles = articleRepository.findAll();

        // Then
        Assertions.assertThat(allArticles).isNotNull();

    }

    @Test
    @DisplayName("INSERT")
    void givenTestData_whenInserting_thenSuccess() throws Exception {
        // Given
        long prevArticleCount = articleRepository.count();

        // When
        articleRepository.save(Article.of("Testing Insert #1", "Testing Insert #1 Content", "#test"));
        long currentArticleCount = articleRepository.count();

        // Then
        Assertions.assertThat(currentArticleCount - prevArticleCount).isEqualTo(1);

    }

    @Test
    @DisplayName("UPDATE")
    void givenTestData_whenUpdating_thenSuccess() throws Exception {
        // Given
        final String UPDATED_HASHTAG = "#SPRING";
        articleRepository.save(Article.of("Testing Update #1", "Testing Update #1 Content", "#update"));

        // When
        Article article = articleRepository.findById(1L).get();
        article.setHashtag(UPDATED_HASHTAG);
        articleRepository.save(article);

        Article finalArticle = articleRepository.findById(1L).get();
        articleRepository.flush(); // flush() 시켜야 update query 가 실행됨
        
        // Then
        Assertions.assertThat(finalArticle).hasFieldOrPropertyWithValue("hashtag", UPDATED_HASHTAG);

    }

    @Test
    @DisplayName("DELETE")
    void givenTestData_whenDeleting_thenSuccess() throws Exception {
        // Given
        Article article = Article.of("Testing Delete #1", "Testing Delete #1 Content", "#delete");
        articleRepository.save(article);

        Article savedArticle = articleRepository.findAll().get(0);
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        // When
        articleRepository.delete(article);

        // Then
        Assertions.assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        Assertions.assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);

    }
}