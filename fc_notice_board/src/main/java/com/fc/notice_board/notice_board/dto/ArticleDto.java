package com.fc.notice_board.notice_board.dto;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {

    private Long id;
    private String title;
    private String content;
    private String hashtag;
    private UserAccountDto userAccountDto;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;

    public ArticleDto(String title, String content, String hashtag, UserAccountDto userAccountDto) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
        this.userAccountDto = userAccountDto;
    }

    public static ArticleDto of(String title, String content, String hashtag, UserAccountDto userAccountDto) {
        return new ArticleDto(title, content, hashtag, userAccountDto);
    }

    public static ArticleDto of(Long id, String title, String content, String hashtag, UserAccountDto userAccountDto) {
        return new ArticleDto(id, title, content, hashtag, userAccountDto, null, null, null, null);
    }

    public static ArticleDto of(Long id, String title, String content, String hashtag, UserAccountDto userAccountDto,
                                LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, title, content, hashtag, userAccountDto, createdAt, createdBy, modifiedAt, modifiedBy);
    }


    /*
    Article -> ArticleDto
     */
    public static ArticleDto from(Article entity){
        return new ArticleDto(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy());
    }

    /*
    ArticleDto -> Article
     */
    public Article toEntity(UserAccount userAccount) {
        return Article.of(
                this.getTitle(),
                this.getContent(),
                this.getHashtag(),
                userAccount,
                this.getCreatedAt(),
                this.getCreatedBy(),
                this.getModifiedAt(),
                this.getModifiedBy()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleDto that = (ArticleDto) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
