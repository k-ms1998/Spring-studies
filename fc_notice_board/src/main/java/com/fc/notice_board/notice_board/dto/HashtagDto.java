package com.fc.notice_board.notice_board.dto;

import com.fc.notice_board.notice_board.domain.Hashtag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HashtagDto {

    private Long id;
    private String hashtagName;
    private LocalDateTime createdAt; // 생성일시
    private String createdBy; // 생성자
    private LocalDateTime modifiedAt; //수정일시
    private String modifiedBy; // 수정자

    public static HashtagDto from(Hashtag hashtag) {
        return new HashtagDto(
                hashtag.getId(),
                hashtag.getHashtagName(),
                hashtag.getCreatedAt(),
                hashtag.getCreatedBy(),
                hashtag.getModifiedAt(),
                hashtag.getModifiedBy()
        );
    }

    public static HashtagDto of(String hashtagName) {
        return new HashtagDto(
                null,
                hashtagName,
                null,
                null,
                null,
                null
        );
    }

    public static HashtagDto of(Long id, String hashtagName, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new HashtagDto(
                id,
                hashtagName,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy
        );
    }

    public Hashtag hashtagName() {
        return Hashtag.of(this.getHashtagName());
    }
}
