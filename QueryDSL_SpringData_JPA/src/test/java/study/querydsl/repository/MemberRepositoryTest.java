package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDTO;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository repository;

    public void initData() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("MemberA", 10, teamA);
        Member memberB = new Member("MemberB", 20, teamA);
        Member memberC = new Member("MemberC", 30, teamB);
        Member memberD = new Member("MemberD", 40, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

    }

    @Test
    void searchWhereTest() {
        initData();

        String usernameParam = null;
        String teamNameParam = "TeamB";
        Integer ageGoeParam = 30;
        Integer ageLoeParam = null;

        MemberSearchCondition condition = new MemberSearchCondition(usernameParam, teamNameParam, ageGoeParam, ageLoeParam);

        List<MemberTeamDTO> result = repository.search(condition);

        for (MemberTeamDTO memberTeam : result) {
            System.out.println("memberTeam = " + memberTeam);
        }

        Assertions.assertThat(result).extracting("username")
                .containsExactly("MemberC", "MemberD");
    }

}