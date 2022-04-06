package jpabook.jpashop.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @MappedSuperClass: 
 * 테이블과 관계 없고, 단순히 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할
 * 주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통 으로 적용하는 정보를 모을 때 사용
 *
 * 주의 사항!
 * 1. @MappedSuperClass는 상속 관계 매핑이 아니다 => 그러므로, parent 테이블 생성 X (ex: practice에서의 p_item 테이블 생성 X)
 * 2. 엔티티 X, 테이블과 매핑 X
 * 3. 상속 받는 자식 클래스에게 매핑 정보만 제공할 뿐
 * 4. find(BaseEntity) X
 */
@MappedSuperclass
public abstract class BaseEntity{

    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    @Column(name = "update_date") //@Column 애너테이션을 통해 BaseEntity를 상속받는 모든 테이블들의 lastModifiedDate column을 필요한 대로 한번에 바꿀수 있음
    private LocalDateTime lastModifiedDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
