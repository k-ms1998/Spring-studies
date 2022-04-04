package hellojpa;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "member_auto_generator",
        sequenceName = "member_auto",
        initialValue = 2, allocationSize = 15
) // initialValue => 처음 값을 저장해 줄때, 2부터 시작해서 저장해줌, allocationSize => 해당 값의 횟수마다 DB를 참조해서 sequence 값을 가져옴
public class MemberAuto {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)   //== MySQL AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_auto_generator")
    private Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
