package com.fc.notice_board.notice_board.service;

import com.fc.notice_board.notice_board.domain.Hashtag;
import com.fc.notice_board.notice_board.repository.HashtagRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("[Hashtag][SERVICE]")
@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {

    @InjectMocks private HashtagService sut;
    @Mock private HashtagRepository hashtagRepository;

    @DisplayName("Parse Content -> Hashtag")
    @ParameterizedTest(name = "[{index}] \"{0}\" => {1}")
    @MethodSource
    void givenContent_whenParsing_thenReturnsUniqueHashtagNames(String input, Set<String> expected) throws Exception {
        // Given - ParameterizedTest

        // When
        Set<String> actual = sut.parseHashtagNames(input);

        // Then
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        then(hashtagRepository).shouldHaveNoInteractions();
    }

    static Stream<Arguments> givenContent_whenParsing_thenReturnsUniqueHashtagNames() {
        return Stream.of(
                Arguments.arguments(null, Set.of()),
                Arguments.arguments("#", Set.of()), // Arguments.arguments(content, expected)
                Arguments.arguments("# ", Set.of()),
                Arguments.arguments(" #", Set.of()),
                Arguments.arguments("#java", Set.of("java")),
                Arguments.arguments("#java_spring", Set.of("java_spring")),
                Arguments.arguments("#_java_spring", Set.of("_java_spring")),
                Arguments.arguments("#java_spring_", Set.of("java_spring_")),
                Arguments.arguments("#-java-spring", Set.of()),
                Arguments.arguments("#java#spring", Set.of("java", "spring")),
                Arguments.arguments(" #java#spring", Set.of("java", "spring")),
                Arguments.arguments(" #java#spring ", Set.of("java", "spring")),
                Arguments.arguments(" #java #spring ", Set.of("java", "spring")),
                Arguments.arguments("#java#spring#intellij", Set.of("java", "spring", "intellij")),
                Arguments.arguments("#java?#spring#intellij", Set.of("java", "spring", "intellij")),
                Arguments.arguments("#java#spring#intellij this is not a hashtag", Set.of("java", "spring", "intellij"))
        );
    }

    @DisplayName("Given Set of hashtagNames - Returns Set of Hashtag")
    @Test
    void givenHashtagNames_whenSearchingHashtag_thenReturnsHashtagSet() throws Exception {
        // Given
        Set<String> hashtagNames = Set.of("java", "spring", "boots");
        given(hashtagRepository.findByHashtagNameIn(hashtagNames))
                .willReturn(List.of(Hashtag.of("java"), Hashtag.of("spring")));

        // When
        Set<Hashtag> actual = sut.findHashtagsByNames(hashtagNames);

        // Then
        Assertions.assertThat(actual).hasSize(2);
        then(hashtagRepository).should().findByHashtagNameIn(hashtagNames);
    }
}