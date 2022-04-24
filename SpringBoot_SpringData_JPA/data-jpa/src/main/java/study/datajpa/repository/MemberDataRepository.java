package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberDataRepository extends JpaRepository<Member, Long> {
//   JpaRepository<Entity, Entity PK Type>
//  @Repository 애노테이션이 없어도 자동으로 JpaRepository를 상속 받으면 Component 스캔이 됨

    /**
     * @param username
     * @param age
     * @return
     * 
     * 메서드를 따로 구현하지 않고, 단순히 선언만 했음에도 작동합니다
     * 쿼리 메서드 기능 1 => 메서드 이름으로 쿼리 생성
     * 메서드 이름에서 관례를 통해서 개발자의 의도를 파악해서 자동으로 의도에 맞게 쿼리문을 작성
     * => SELECT *FROM member m WHERE m.username = username AND m.age = age LIMIT 2;
     * 
     * 이때, Username -> username, Age -> age로 속성들이 매칭 되기 때문에 메서드 이름을 제대로 작성하는 것이 매우 중요
     * ex: 메서드를 findTop2ByUsernameAndAgeGreaterThan 으로 하면 UserName -> userName 이름의 속성을 찾는데 Member에는 존재 X => 에러 발생
     * 
     * 메서드 이름을 통해 페이징도 가능
     * 
     * 스프링 메뉴얼을 통해 어떤 식으로 메서드 이름을 작성해서 쿼리문으로 변경되는지 확인
     * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
     * 
     * 쿼리가 단순하고, 조건이 많지 않을떄 사용하기 적합함
     * 
     */
    List<Member> findTop2ByUsernameAndAgeGreaterThan(String username, int age);
    
}
