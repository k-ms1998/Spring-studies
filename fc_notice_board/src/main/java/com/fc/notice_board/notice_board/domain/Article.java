package com.fc.notice_board.notice_board.domain;

import com.fc.notice_board.notice_board.dto.ArticleDto;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String title; // 제복

    @Setter
    @Column(nullable = false, length = 10000)
    private String content; // 본문

    @OrderBy("id")
    @ToString.Exclude
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @ToString.Exclude
    @JoinTable(
            name = "article_hashtag", // ManyToMany 사이의 매핑 테이블의 이름 설정
            joinColumns = @JoinColumn(name = "articleId"),
            inverseJoinColumns = @JoinColumn(name = "hashtagId")
    ) // ManyToMany 에서 주인을 Article 로 설정 -> Article 을 기준으로 해시태그가 생성되기 때문에
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final Set<Hashtag> hashtags = new LinkedHashSet<>();

    @CreatedDate
    private LocalDateTime createdAt; // 생성일시

    @CreatedBy
    private String createdBy; // 생성자

    @LastModifiedDate
    private LocalDateTime modifiedAt; //수정일시

    @LastModifiedBy
    private String modifiedBy; // 수정자

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount;

    private Article(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Article(String title, String content, UserAccount userAccount) {
        this.title = title;
        this.content = content;
        this.userAccount = userAccount;
    }

    public Article(String title, String content, UserAccount userAccount,
                   LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        this.title = title;
        this.content = content;
        this.userAccount = userAccount;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }

    public Article(Long id, String title, String content, UserAccount userAccount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userAccount = userAccount;
    }

    public static Article of(String title, String content) {
        return new Article(title, content);
    }

    public static Article of(String title, String content, UserAccount userAccount) {
        return new Article(title, content, userAccount);
    }

    public static Article of(Long id, String title, String content, UserAccount userAccount) {
        return new Article(id, title, content, userAccount);
    }

    public static Article of(String title, String content, UserAccount userAccount,
                   LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new Article(title, content, userAccount, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public void update(ArticleDto dto) {
        if(dto.getTitle() != null){
            this.title = dto.getTitle();
        }
        if(dto.getContent() != null){
            this.content = dto.getContent();
        }
    }

    public void addHashtags(Hashtag hashtag) {
        this.getHashtags().add(hashtag);
    }

    public void addHashtags(Collection<Hashtag> hashtag) {
        this.getHashtags().addAll(hashtag);
    }

    public void clearHashtags() {
        this.getHashtags().clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return getId().equals(article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
