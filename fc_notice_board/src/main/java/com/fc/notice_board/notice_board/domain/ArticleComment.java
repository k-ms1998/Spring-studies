package com.fc.notice_board.notice_board.domain;

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
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ArticleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    private Article article; // 게시글 (ID)

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 본문

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

    @Setter
    @Column(nullable = true, updatable = false)
    private Long parentCommentId;

    @ToString.Exclude
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL) // 부모 댓글이 삭제되면 자식 댓글들도 모두 삭제
    private Set<ArticleComment> childComments = new LinkedHashSet<>(); // 순서가 중요하기 때문에 LinkedHashSet

    private ArticleComment(Article article, String content) {
        this.article = article;
        this.content = content;
    }

    public static ArticleComment of(Article article, String content) {
        return new ArticleComment(article, content);
    }

    public ArticleComment(Article article, String content, LocalDateTime createdAt, String createdBy, UserAccount userAccount, Long parentCommentId) {
        this.article = article;
        this.content = content;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.userAccount = userAccount;
        this.parentCommentId = parentCommentId;
    }

    public static ArticleComment of(Article article, String content, LocalDateTime createdAt, String createdBy, UserAccount userAccount) {
        return new ArticleComment(article, content, createdAt, createdBy, userAccount, null);
    }

    public static ArticleComment of(Article article, String content, LocalDateTime createdAt, String createdBy, UserAccount userAccount, Long parentCommentId) {
        return new ArticleComment(article, content, createdAt, createdBy, userAccount, parentCommentId);
    }

    public void addChildComment(ArticleComment childComment) {
        childComment.setParentCommentId(this.getId());
        this.getChildComments().add(childComment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleComment that = (ArticleComment) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
