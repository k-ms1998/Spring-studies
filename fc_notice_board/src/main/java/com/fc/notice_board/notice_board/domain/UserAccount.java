package com.fc.notice_board.notice_board.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "email", unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String userId;

    @NotNull
    @Column(length = 255)
    private String userPassword;

    @Setter
    @Column(length = 100)
    private String email;

    @Setter
    @Column(length = 100)
    private String nickname;

    @Setter
    @Column(length = 255)
    private String memo;

    @CreatedDate
    private LocalDateTime createdAt; // 생성일시

    @CreatedBy
    private String createdBy; // 생성자

    @LastModifiedDate
    private LocalDateTime modifiedAt; //수정일시

    @LastModifiedBy
    private String modifiedBy; // 수정자
}
