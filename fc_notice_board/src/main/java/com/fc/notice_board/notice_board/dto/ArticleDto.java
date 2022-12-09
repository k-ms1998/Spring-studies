package com.fc.notice_board.notice_board.dto;

import com.fc.notice_board.notice_board.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        return new ArticleDto(id, title, content, hashtag, userAccountDto);
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
                UserAccountDto.from(entity.getUserAccount()));
    }

    /*
    ArticleDto -> Article
     */
    public Article toEntity() {
        return Article.of(
                this.getTitle(),
                this.getContent(),
                this.getHashtag(),
                this.userAccountDto.toEntity()
        );
    }


}
