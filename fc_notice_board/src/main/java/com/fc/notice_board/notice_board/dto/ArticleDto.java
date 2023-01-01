package com.fc.notice_board.notice_board.dto;

import com.fc.notice_board.notice_board.domain.Article;
import com.fc.notice_board.notice_board.domain.Hashtag;
import com.fc.notice_board.notice_board.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {

    private Long id;
    private String title;
    private String content;
    private Set<HashtagDto> hashtagsDtos = new LinkedHashSet<>();
    private UserAccountDto userAccountDto;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;

    public ArticleDto(String title, String content, Set<HashtagDto> hashtagDtos, UserAccountDto userAccountDto) {
        this.title = title;
        this.content = content;
        this.hashtagsDtos = hashtagDtos;
        this.userAccountDto = userAccountDto;
    }

    public static ArticleDto of(String title, String content, Set<HashtagDto> hashtagDtos, UserAccountDto userAccountDto) {
        return new ArticleDto(title, content, hashtagDtos, userAccountDto);
    }

    public static ArticleDto of(Long id, String title, String content, Set<HashtagDto> hashtagDtos, UserAccountDto userAccountDto) {
        return new ArticleDto(id, title, content, hashtagDtos, userAccountDto, null, null, null, null);
    }

    public static ArticleDto of(Long id, String title, String content, Set<HashtagDto> hashtagDtos, UserAccountDto userAccountDto,
                                LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, title, content, hashtagDtos, userAccountDto, createdAt, createdBy, modifiedAt, modifiedBy);
    }


    /*
    Article -> ArticleDto
     */
    public static ArticleDto from(Article entity){
        return new ArticleDto(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet()),
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
