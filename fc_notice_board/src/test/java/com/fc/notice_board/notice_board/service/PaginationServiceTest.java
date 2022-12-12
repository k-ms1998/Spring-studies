package com.fc.notice_board.notice_board.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;


@DisplayName("[Pagination Service]")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class)
class PaginationServiceTest {

    private final PaginationService sut;

    public PaginationServiceTest(@Autowired PaginationService paginationService) {
        this.sut = paginationService;
    }

    /**
     * 메서드로 파라미터들을 주입해주는 방식으로 테스트
     */
    @DisplayName("Create Paging Bar List")
    @ParameterizedTest(name = "[{index}] (currentPageNumber={0}, totalPages={1}) -> Page Numbers={2}")
    @MethodSource
    void givenCurrentPageNumberAndTotalPages_whenCalculating_thenReturnsPaginationBarNumbers(
            int currentPageNumber, int totalPages, List<Integer> expected
    ) throws Exception {
        // Given

        // When
        List<Integer> actual = sut.getPaginationBarNumbers(currentPageNumber, totalPages);

        // Then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> givenCurrentPageNumberAndTotalPages_whenCalculating_thenReturnsPaginationBarNumbers() {
        return Stream.of(
                Arguments.arguments(0, 13, List.of(0, 1, 2, 3, 4)), // Arguments.arguments(currentPageNumber, totalPages, expected)
                Arguments.arguments(1, 13, List.of(0, 1, 2, 3, 4)),
                Arguments.arguments(2, 13, List.of(0, 1, 2, 3, 4)),
                Arguments.arguments(3, 13, List.of(1, 2, 3, 4, 5)),
                Arguments.arguments(4, 13, List.of(2, 3, 4, 5, 6)),
                Arguments.arguments(5, 13, List.of(3, 4, 5, 6, 7)),
                Arguments.arguments(6, 13, List.of(4, 5, 6, 7, 8)),
                Arguments.arguments(10, 13, List.of(8, 9, 10, 11, 12)),
                Arguments.arguments(12, 13, List.of(10, 11, 12))
        );
    }

    @DisplayName("Get Pagination Bar Length")
    @Test
    void givenNothing_whenCalling_thenReturnsCurrentBarLength() throws Exception {
        // Given

        // When
        int actual = sut.currentBarLength();

        // Then
        Assertions.assertThat(actual).isEqualTo(5);
    }
}