package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class MemberRepository {
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        log.info("save: member={}", member);
        return member;
    }

    public Member findById(Long id) {
        Member member = store.get(id);

        return member;
    }

    public List<Member> findAll() {
        return store.values().stream().collect(Collectors.toList());
    }

    public Optional<Member> findByLoginId(String loginId) {
        return store.values().stream()
                .filter(v -> v.getLoginId().equals(loginId)).findAny();
    }
}

