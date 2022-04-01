package hellojpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity //JPA를 사용한다고 알려주는 애노테이션
@Table(name = "member") // 클래스 이름이랑 테이블 이름이 같으면 생략 가능
public class Member {
    /**
     * 쿼리 문으로 DB에 테이블 먼저 생성:
     * create table Member (
     * id bigint not null,
     * name varchar(255),
     * primary key (id)
     * );
     */

    @Id // PK(Primary Key) 인것을 알려줌
    @Column(name = "id") // 변수 이름이랑 column name 이랑 같으면 생략 가능
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
