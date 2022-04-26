package study.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass // BaseEntity의 속성들을 상속 받는 엔티티 들이 속성 값들로 가질수 있도록 해줌
public class JpaBaseEntity {

    @Column(updatable = false) //해당 값을 한번 생성하면 변할수 없도록 해줌
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @PrePersist //persist 하기 전에 자동으로 메서드가 호출되도록 해줌
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;

    }

    @PreUpdate  //update 쿼리가 실행 되지 전에 메서드가 호출되도록 해줌
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
