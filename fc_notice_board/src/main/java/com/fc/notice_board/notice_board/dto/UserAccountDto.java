package com.fc.notice_board.notice_board.dto;

import com.fc.notice_board.notice_board.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDto {

    private String userId;
    private String userPassword;
    private String email;
    private String nickname;
    private String memo;
    private String createdBy;
    private String modifiedBy;

    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, String memo){
        return new UserAccountDto(userId, userPassword, email, nickname, memo, null, null);
    }

    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, String memo, String createdBy, String modifiedBy){
        return new UserAccountDto(userId, userPassword, email, nickname, memo, createdBy, modifiedBy);
    }

    public static UserAccountDto from(UserAccount entity) {
        return new UserAccountDto(
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getMemo(),
                entity.getCreatedBy(),
                entity.getModifiedBy()
        );
    }

    public UserAccount toEntity() {
        return UserAccount.of(
                this.getUserId(),
                this.getUserPassword(),
                this.getEmail(),
                this.getNickname(),
                this.getMemo(),
                this.getCreatedBy(),
                this.getModifiedBy()
        );
    }
}
