package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.dto.MemberDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberDataRepositoryTest {

    /**
     * memberDataRepository.getClass() 출력 시 -> class com.sun.proxy.$ProxyXXX
     * 아무것도 구현하지 않은 인터페이스 임에도 불구하고 save, findById() 등 이 작동하는 이유는
     * 해당 인터페이스를 보고 스프링이 자동으로 프록시로 구현 클래스를 만들어서 관계 주입 시켜줌
     */
    @Autowired
    private MemberDataRepository memberDataRepository;

    @Autowired
    private TeamDataRepository teamDataRepository;

    @AfterEach
    public void init() {
        memberDataRepository.deleteAll();
    }

    @Test
    void testMember() {
        Member member = new Member("memberA");

        Member saveMember = memberDataRepository.save(member);
        Member findMember = memberDataRepository.findById(member.getId()).get();

        assertThat(saveMember).isEqualTo(findMember);
    }

    @Test
    void basicCRUDTest() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberDataRepository.save(member1);
        memberDataRepository.save(member2);

        Member findMember1 = memberDataRepository.findById(member1.getId()).get();
        Member findMember2 = memberDataRepository.findById(member2.getId()).get();

        //단건 조회 검증
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> members = memberDataRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        //카운트 검증
        long count = memberDataRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberDataRepository.delete(member1);
        org.junit.jupiter.api.Assertions.assertThrows(NoSuchElementException.class, () -> memberDataRepository.findById(member1.getId()).get());

    }

    @Test
    void findByNameAndAge() {
        Member memberA = new Member("memberA", 20);
        Member memberB = new Member("memberB", 26);
        Member memberC = new Member("memberB", 30);

        memberDataRepository.save(memberA);
        memberDataRepository.save(memberB);
        memberDataRepository.save(memberC);

        List<Member> findMembers = memberDataRepository.findTop2ByUsernameAndAgeGreaterThan("memberB", 25);
        assertThat(findMembers.size()).isEqualTo(2);
    }


    @Test
    void findByUsernameNamedQuery() {
        Member memberA = new Member("memberA", 20);
        Member memberB = new Member("memberB", 26);
        Member memberC = new Member("memberB", 30);

        memberDataRepository.save(memberA);
        memberDataRepository.save(memberB);
        memberDataRepository.save(memberC);


        List<Member> findMembers = memberDataRepository.findByUsername("memberB");
        assertThat(findMembers.size()).isEqualTo(2);
    }

    @Test
    void findByUsernameQuery() {
        Member memberA = new Member("memberA", 20);
        Member memberB = new Member("memberB", 26);
        Member memberC = new Member("memberB", 30);

        memberDataRepository.save(memberA);
        memberDataRepository.save(memberB);
        memberDataRepository.save(memberC);


        List<Member> findMembers = memberDataRepository.findUser("memberB", 20);
        assertThat(findMembers.size()).isEqualTo(2);
    }

    @Test
    void findUsernameList() {
        Member memberA = new Member("memberA", 20);
        Member memberB = new Member("memberB", 26);
        Member memberC = new Member("memberB", 30);

        memberDataRepository.save(memberA);
        memberDataRepository.save(memberB);
        memberDataRepository.save(memberC);


        List<String> findMembers = memberDataRepository.findUsernameList();
        findMembers.stream()
                .forEach(m -> {
                    System.out.println("m = " + m);
                });
        assertThat(findMembers.size()).isEqualTo(3);
    }

    @Test
    void findUsernameAgeTeamnameList() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        teamDataRepository.save(teamA);
        teamDataRepository.save(teamB);


        Member memberA = new Member("memberA", 20, teamA);
        Member memberB = new Member("memberB", 26, teamB);
        Member memberC = new Member("memberB", 30, teamB);

        memberDataRepository.save(memberA);
        memberDataRepository.save(memberB);
        memberDataRepository.save(memberC);


        List<MemberDTO> findMembers = memberDataRepository.findUsernameAgeTeamnameList();
        findMembers.stream()
                .forEach(m -> {
                    System.out.println("m.getUsername() = " + m.getUsername() +
                            " | m.getAge = " + m.getAge() +
                            " | m.getTeam = " + m.getTeamName());
                });
        assertThat(findMembers.size()).isEqualTo(3);
    }

    @Test
    void findByNames() {
        Member memberA = new Member("memberA", 20);
        Member memberB = new Member("memberB", 26);
        Member memberC = new Member("memberC", 30);

        memberDataRepository.save(memberA);
        memberDataRepository.save(memberB);
        memberDataRepository.save(memberC);

        List<String> usernames = new ArrayList<>();
        usernames.add("memberA");
        usernames.add("memberB");

        List<Member> findMembers = memberDataRepository.findByNames(usernames);
        findMembers.stream()
                .forEach(m -> {
                    System.out.println("m = " + m);
                });
        assertThat(findMembers.size()).isEqualTo(2);
    }

    @Test
    public void page() throws Exception {
        Team teamA = new Team("TeamA");
        teamDataRepository.save(teamA);

        //given
        memberDataRepository.save(new Member("member0", 10, teamA));
        memberDataRepository.save(new Member("member1", 10, teamA));
        memberDataRepository.save(new Member("member2", 10, teamA));
        memberDataRepository.save(new Member("member3", 10, teamA));
        memberDataRepository.save(new Member("member4", 10, teamA));
        memberDataRepository.save(new Member("member5", 10, teamA));
        memberDataRepository.save(new Member("member6", 10, teamA));
        memberDataRepository.save(new Member("member7", 10, teamA));
        memberDataRepository.save(new Member("member8", 10, teamA));
        memberDataRepository.save(new Member("member9", 10, teamA));

        //when
        /**
         * PageRequest implements AbstractPageRequest && AbstractPageRequest implements Pageable
         * ORDER BY username DESC OFFSET 6 LIMIT 3;
         */
        PageRequest pageRequest = PageRequest.of(2, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberDataRepository.findByAge(10, pageRequest);
        /**
         * 실행되는 쿼리문:
         * 1. select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_ 
         * from member member0_ where member0_.age=10 order by member0_.username desc limit 3 offset 6;
         * 
         * 2. select count(member0_.member_id) as col_0_0_ from member member0_ where member0_.age=10;
         * => 페이징을 하지 않은 경우에 반환 되는 총 튜플의 갯수를 알려주기 위해 count(*)를 포함한 쿼리문도 같이 실행 됨
         *
         * Page #1:
         * member.username = {member9, member8, member7}
         *
         * Page #2:
         * member.username = {member6, member5, member4}
         *
         * Page #3:
         * member.username = {member3, member2, member1}
         *
         * Page #4
         * member.username = {member0}
         */

        /**
         * Page를 이용할 경우, 바로 page.map을 통해서 DTO로 변환 할 수 있다
         * 실무에서는 엔티티 자체를 클라이언트에 반환하는 것은 위험함으로, DTO로 변환해서 반환하는 것이 권장됨
         */
        Page<MemberDTO> pageDTO = page.map(member -> new MemberDTO(member.getUsername(), member.getAge(), member.getTeam()));
        for (MemberDTO memberDTO : pageDTO) {
            System.out.println("memberDTO = " + memberDTO);
        }

        System.out.println("=============================================");
        //then
        List<Member> content = page.getContent(); // == Page #3
        System.out.println("content.size() = " + content.size());
        for (Member member : content) {
            System.out.println("member = " + member);
        }// member.username = {member3, member2, member1}

        long totalElements = page.getTotalElements(); // == 전체 데이터 수
        System.out.println("totalElements = " + totalElements);

        int number = page.getNumber(); //페이지 번호
        System.out.println("number = " + number);

        int totalPages = page.getTotalPages();
        System.out.println("totalPages = " + totalPages);

        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(10); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(2); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(4); //전체 페이지 번호
        assertThat(page.isFirst()).isFalse(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?


    }

    @Test
    public void slicing(){

        //given
        memberDataRepository.save(new Member("member0", 10));
        memberDataRepository.save(new Member("member1", 10));
        memberDataRepository.save(new Member("member2", 10));
        memberDataRepository.save(new Member("member3", 10));
        memberDataRepository.save(new Member("member4", 10));
        memberDataRepository.save(new Member("member5", 10));
        memberDataRepository.save(new Member("member6", 10));
        memberDataRepository.save(new Member("member7", 10));
        memberDataRepository.save(new Member("member8", 10));
        memberDataRepository.save(new Member("member9", 10));


        //when
        PageRequest pageRequest = PageRequest.of(2, 3, Sort.by(Sort.Direction.DESC, "username"));
        Slice<Member> slice = memberDataRepository.findSliceByAge(10, pageRequest);
        /**
         * 실행되는 쿼리문:
         * 1. select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_
         * from member member0_ where member0_.age=10 order by member0_.username desc limit 4 offset 6;
         * => Page와 다르게, slice는 size+1 만큼 LIMIT을 실행함
         */

        System.out.println("=============================================");
        //then
        List<Member> content = slice.getContent(); // == Page #3
        System.out.println("content.size() = " + content.size());
        for (Member member : content) {
            System.out.println("member = " + member);
        }// member.username = {member3, member2, member1}

        int number = slice.getNumber(); //페이지 번호
        System.out.println("number = " + number);


        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(slice.getNumber()).isEqualTo(2); //페이지 번호
        assertThat(slice.isFirst()).isFalse(); //첫번째 항목인가?
        assertThat(slice.hasNext()).isTrue(); //다음 페이지가 있는가?


    }

    @Test
    void bulkUpdate() {

        //given
        memberDataRepository.save(new Member("member0", 10));
        memberDataRepository.save(new Member("member1", 19));
        memberDataRepository.save(new Member("member2", 20));
        memberDataRepository.save(new Member("member3", 21));
        memberDataRepository.save(new Member("member4", 40));

        //when
        int resultCount = memberDataRepository.bulkAgePlus(20);

        List<Member> member4 = memberDataRepository.findByUsername("member4");
        System.out.println("member4.age = " + member4.get(0).getAge());
        
        Assertions.assertThat(resultCount).isEqualTo(3);


    }
}