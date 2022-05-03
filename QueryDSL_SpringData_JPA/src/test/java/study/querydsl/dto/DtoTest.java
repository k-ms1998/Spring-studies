package study.querydsl.dto;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class DtoTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory factory;
    QMember member = QMember.member;

    @BeforeEach
    public void before() {
        factory = new JPAQueryFactory(em);

        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("MemberA", 25, teamA);
        Member memberB = new Member("MemberB", 35, teamA);
        Member memberC = new Member("MemberC", 45, teamA);
        Member memberD = new Member("MemberD", 55, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

    }

    @Test
    void findDtoBySetter() {
        //Setter로 값을 가져옴
        List<MemberDTO> result = factory
                .select(Projections
                        .bean(MemberDTO.class, member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDTO memberDTO : result) {
            System.out.println("memberDTO = " + memberDTO);
        }

        Assertions.assertThat(result).extracting("username", "age")
                .containsExactly(new Tuple("MemberA", 25),
                        new Tuple("MemberB", 35),
                        new Tuple("MemberC", 45),
                        new Tuple("MemberD", 55));
    }

    @Test
    void findDtoByField() {
        //값들을 바로 속성 값으로 넣어줌
        List<MemberDTO> result = factory
                .select(Projections
                        .fields(MemberDTO.class, member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDTO memberDTO : result) {
            System.out.println("memberDTO = " + memberDTO);
        }

        Assertions.assertThat(result).extracting("username", "age")
                .containsExactly(new Tuple("MemberA", 25),
                        new Tuple("MemberB", 35),
                        new Tuple("MemberC", 45),
                        new Tuple("MemberD", 55));
    }

    @Test
    void findDtoByConstructor() {
        //생성자를 이용해서 값을 넝어줌 (순수 JPA에서와 비슷함) -> JPA 일때 처럼 필요한 생성자가 필수
        List<MemberDTO> result = factory
                .select(Projections
                        .constructor(MemberDTO.class, member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDTO memberDTO : result) {
            System.out.println("memberDTO = " + memberDTO);
        }

        Assertions.assertThat(result).extracting("username", "age")
                .containsExactly(new Tuple("MemberA", 25),
                        new Tuple("MemberB", 35),
                        new Tuple("MemberC", 45),
                        new Tuple("MemberD", 55));
    }

    @Test
    void findByUserDto() {
        //Member -> UserDto로 변환해서 반환하고 싶음 -> But, UserDto에는 username을 name이라고 저장 -> field, bean을 사용하면 오류 발생 -> as(alias) 으로 UserDto의 필드 값에 맞춰서 별칭을 정해줌
        // age는 Member와 UserDto가 같으므로 별칭 사용 X
        // 별칭을 사용하지 않으면, UserDto.name 에 NULL 저장
        // 생성자(constructors()) 방식을 사용하면 별칭을 사용하지 않아도 됨
        List<UserDTO> result = factory
                .select(Projections.fields(UserDTO.class, member.username.as("name"), member.age))
                .from(member)
                .fetch();

        for (UserDTO userDTO : result) {
            System.out.println("memberDTO = " + userDTO);
        }

        Assertions.assertThat(result).extracting("name", "age")
                .containsExactly(new Tuple("MemberA", 25),
                        new Tuple("MemberB", 35),
                        new Tuple("MemberC", 45),
                        new Tuple("MemberD", 55));

    }

    @Test
    void findByUserDtoSubQueryAlias() {
        QMember subMember = new QMember("sub");

        // 만약에, UserDto의 age에 Member의 최가 age를 넣고 싶음 -> Member의 username도 같이 반환하기 때문에 age의 최대값을 가져올때 select에서 서브쿼리를 사용해야됨
        // 서브쿼리의 값을 UserDto로 반환하기 위해서는 서브쿼리의 결과 값에도 별칭 사용
        // ExpressionUtils.as(JPAExpressions
        //  .select(subMember.age.max())
        //  .from(subMember), "age")))
        List<UserDTO> result = factory
                .select(Projections.fields(UserDTO.class,
                        member.username.as("name"),
                        ExpressionUtils.as(JPAExpressions
                                .select(subMember.age.max())
                                .from(subMember), "age")))
                .from(member)
                .fetch();


        for (UserDTO userDTO : result) {
            System.out.println("memberDTO = " + userDTO);
        }

        Assertions.assertThat(result).extracting("name", "age")
                .containsExactly(new Tuple("MemberA", 55),
                        new Tuple("MemberB", 55),
                        new Tuple("MemberC", 55),
                        new Tuple("MemberD", 55));

    }



}
