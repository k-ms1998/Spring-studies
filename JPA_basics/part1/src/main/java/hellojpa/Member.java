package hellojpa;

import javax.persistence.*;
import java.util.Date;

@Entity //JPA를 사용한다고 알려주는 애노테이션
@Table(name = "member") // 클래스 이름이랑 테이블 이름이 같으면 생략 가능
public class Member {
    @Id // PK(Primary Key) 인것을 알려줌
    @Column(name = "id") // 변수 이름이랑 column name 이랑 같으면 생략 가능
    private Long id;

    @Column(name = "name", nullable = false, updatable = true, length = 10) //updatable=true가 DEFAULT; false로 하면 JPA에서 해당 column의 값 수정 X
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING) //무조건 EnumType.STRING을 쓰도록 주의
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;

    public Member() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
