package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
public class Item extends DataBaseTimeEntity implements Persistable<String>{

    @Id
    private String id;

    protected Item() {
    }

    public Item(String id) {
        this.id = id;
    }

    /**
     * Persistable 을 상속 받아서 isNew() 메서드에서 해당 엔티티가 새로운 엔티티인지 아닌지를 판별해 줄 수 있다.
     * 새로운 엔티티이면, createdDate가 NULL이기 때문에, createdDate가 NULL인지 아닌지에 따라서 판별하도록하면 편리합니다.
     */
    @Override
    public boolean isNew() {
        return this.createdDate == null;
    }
}
