package com.fc.notice_board.notice_board.service;

import com.fc.notice_board.notice_board.domain.Hashtag;
import com.fc.notice_board.notice_board.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    @Transactional
    public Set<String> parseHashtagNames(String content) {
        if (content == null) {
            return Set.of();
        }

        Pattern pattern = Pattern.compile("#[\\w가-힣]+"); // '#' 다음에 하나 이상의 한국어 단어가 있을때 정규 표현식
        Matcher matcher = pattern.matcher(content.strip()); // content 의 앞뒤에 있는 공백 제거

        Set<String> result = new HashSet<>();
        while (matcher.find()) { // 정규식을 만족시키는 단어가 발견되면
            result.add(matcher.group().replace("#", ""));
        }

        return Set.copyOf(result); // result 그대로 반환해도 됨; 이렇게 copy 해서 보내면 result 는 불변함을 유지할 수 있음
    }

    public Optional<Hashtag> findHashtagByName(String hashtagName) {
        return hashtagRepository.findByHashtagName(hashtagName);
    }

    public Set<Hashtag> findHashtagsByNames(Set<String> hashtagNames) {
        return new HashSet<>(hashtagRepository.findByHashtagNameIn(hashtagNames));
    }

    /**
     * 해당 해시태그가 어떤 게시글에서도 쓰이지 않고 있으면 삭제
     * @param hashtagId
     */
    @Transactional
    public void deleteHashtagWithoutArticles(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);

        /*
        양방향 바인딩으로 되어 있으므로, 해시태그가 어느 게시글들에 쓰이는지 저장하고 있음
         */
        if (hashtag.getArticles().isEmpty()) {
            hashtagRepository.deleteById(hashtagId);
        }
    }
}