package com.fc.notice_board.notice_board.repository;

import com.fc.notice_board.notice_board.config.JpaConfig;
import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.ArticleComment;
import com.fc.notice_board.notice_board.domain.Hashtag;
import com.fc.notice_board.notice_board.domain.UserAccount;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DisplayName("JPA 연결 테스트")
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;

    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository,
            @Autowired UserAccountRepository userAccountRepository,
            @Autowired HashtagRepository hashtagRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
        this.hashtagRepository = hashtagRepository;
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
        UserAccount userAccount = userAccountRepository.save(new UserAccount("kms", "password", "email", "nickname", "memo", null, null));

        // When
        articleRepository.save(Article.of("Testing Insert #1", "Testing Insert #1 Content", userAccount));
        long currentArticleCount = articleRepository.count();

        // Then
        Assertions.assertThat(currentArticleCount - prevArticleCount).isEqualTo(1);

    }

    @Test
    @DisplayName("UPDATE")
    @Transactional
    void givenTestData_whenUpdating_thenSuccess() throws Exception {
        // Given
        final String UPDATED_HASHTAG = "#SPRING";
        UserAccount userAccount = userAccountRepository.save(new UserAccount("kms", "password", "email", "nickname", "memo", null, null));
        Article savedArticle = articleRepository.save(Article.of("Testing Update #1", "Testing Update #1 Content", userAccount));

        // When
        savedArticle.addHashtags(Hashtag.of(UPDATED_HASHTAG));
        articleRepository.save(savedArticle);

        Article finalArticle = articleRepository.getReferenceById(savedArticle.getId());
        articleRepository.flush(); // flush() 시켜야 update query 가 실행됨
        
        // Then
        Assertions.assertThat(finalArticle.getHashtags().stream().collect(Collectors.toList()).get(0))
                .hasFieldOrPropertyWithValue("hashtagName", UPDATED_HASHTAG);

    }

    @Test
    @DisplayName("DELETE")
    void givenTestData_whenDeleting_thenSuccess() throws Exception {
        // Given
        UserAccount userAccount = userAccountRepository.save(new UserAccount("kms", "password", "email", "nickname", "memo", null, null));
        Article article = Article.of("Testing Delete #1", "Testing Delete #1 Content", userAccount);
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

    @DisplayName("[ArticleComment] - SELECT")
    @Test
    void givenParentCommentId_whenSelecting_thenReturnsChildComments() throws Exception {
        // Given
        Long parentCommentId = 1L;
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("kms", "password", "email", "nickname", "memo", "kms", "kms"));
        Article article = articleRepository.save(Article.of("title", "content", userAccount));
        articleCommentRepository.save(ArticleComment.of(article, "parent comment", LocalDateTime.now(), "kms", userAccount));

        // When
        Optional<ArticleComment> parentArticleComment = articleCommentRepository.findById(parentCommentId);

        Assertions.assertThat(parentArticleComment).get()
                .hasFieldOrPropertyWithValue("parentCommentId", null)
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                    .hasSize(0);
        // Then

    }

    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfig{
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("kms");
        }
    }
}