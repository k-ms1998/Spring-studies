package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;
import study.datajpa.repository.dto.MemberDTO;

import javax.persistence.QueryHint;
import java.util.Collection;
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

    /**
     * @param username
     * @return
     * 쿼리 메서드 기능 2 => NamedQuery
     *
     * 엔티티에서 정의한 @NamedQuery를 따로 메서드로 구현하지 않고, @Param 에노테이션으로 바로 파라미터 값을 넘겨줘서 쿼리문을 실행
     * 
     * @Query 에노테이션 생략 가능
     * -> SpringData에서 먼저 엔티티+메서드 이름의 @NamedQuery가 있는지 확인 -> Member+findByUsername => Member.findByUsername의 @NamedQuery를 찾음
     * -> 일치하는 @NamedQuery가 없으면 위에서 봤던 메서드 이름으로 쿼리 생성
     */
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * 
     * @param username
     * @param age
     * @return
     * 
     * 쿼리 메서드 기능 2 => @Query로 바로 쿼리 문 작성하기
     * 실무에서 가장 많이 사용되는 방법
     * 
     * @Param 애노테이션을 쿼리의 파라미터 값들을 바로 주입해줘서 쿼리가 실행 됩니다
     * @NamedQuery의 모든 장점들을 갖고 있지만 사용하기 더 단순
     * 메서드 이름으로 쿼리가 작성 되는 방식보다 더 복잡한 쿼리들을 쉽게 작성 할 수 있음(메서드 이름으로 쿼리가 생성 되는 방식은 파라미터 값들이 많아지면 복잡해짐)
     */
    @Query("select m from Member m where m.username = :username and m.age > :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList(); //Member들의 username만 반환하기

    @Query("select new study.datajpa.repository.dto.MemberDTO(m.username, m.age, m.team) from Member m")
    List<MemberDTO> findUsernameAgeTeamnameList(); //Member들의 username만 반환하기

    @Query("select m from Member m where m.username in :usernames")
    List<Member> findByNames(@Param("usernames") Collection<String> usernames);

    /**
     * 메서드 이름으로 쿼리 생성
     * Pageable에 쿼리에 대한 페이징 조건이 들어감
     */
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    /**
     * 하나의 데이터만 변경 했을때는 Dirty Checking에 의해서 값이 변경됨을 감지하고 DB와 영속성 컨텍스트를 모두 참고 했었습니다.
     * 그러나, 벌크 수정/삭제를 할때는 영속성 컨텍스트를 무시하고 바로 DB에 접근해서 값들을 변경 합니다.
     * 그렇기 때문에, 수정할 데이터가 영속성 컨텍스트에 있을때 벌크 연산을 하면, 해당 데이터가 DB에서의 값과 영속성 컨텍스트에서의 값이 불일치 합니다
     * 그러므로, @Modifying 애노테이션에 clearAutomatically = true로 설정해서 벌크 연산 이후에 영속성 컨텍스트를 초기화 시켜줍니다 => 쿼리 실행 후 자동으로 영속성 컨텍스트 초기화 시켜줌
     * @param age
     * @return
     */
    @Modifying(clearAutomatically = true) // == executeUpdate(); 벌크성 수정 & 삭제 쿼리는 해당 애노테이션이 없으면 오류 발생
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * @EntityGraph 애노테이션으로 SpringData에서 JOIN FETCH를 메서드 호출만을 통해서 실행 할 수 있음
     * @return
     */
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);
}
