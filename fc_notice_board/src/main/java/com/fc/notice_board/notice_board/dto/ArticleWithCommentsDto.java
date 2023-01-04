package com.fc.notice_board.notice_board.dto;

import com.fc.notice_board.notice_board.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleWithCommentsDto {

    Long id;
    UserAccountDto userAccountDto;
    ArticleDto articleDto;
    Set<ArticleCommentDto> articleCommentDtos;

    public ArticleWithCommentsDto(UserAccountDto userAccountDto, ArticleDto articleDto, Set<ArticleCommentDto> articleCommentDtos) {
        this.userAccountDto = userAccountDto;
        this.articleDto = articleDto;
        this.articleCommentDtos = articleCommentDtos;
    }

    public static ArticleWithCommentsDto of(UserAccountDto userAccountDto, ArticleDto articleDto, Set<ArticleCommentDto> articleCommentDtos) {
        return new ArticleWithCommentsDto(userAccountDto, articleDto, articleCommentDtos);
    }

    public static ArticleWithCommentsDto from(Article entity) {
        return new ArticleWithCommentsDto(
                entity.getId(), // id
                UserAccountDto.from(entity.getUserAccount()),   // userAccountDto
                ArticleDto.from(entity),    // articleDto
                entity.getArticleComments().stream()
                        .map(ArticleCommentDto::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new)) // articleCommentDtos
        );
    }


}
