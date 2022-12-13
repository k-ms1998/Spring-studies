package com.fc.notice_board.notice_board.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;

    /**
     * 뷰에서 페이지 수를 나타내는 바의 길이는 BAR_LENGTH 가 되도록 설정
     * 현재 페이지(currentPageNumber) 가 중간에 오도록 설정
     *  -> ex: 총 10개의 페이지가 있고, 현재 페이지가 5이면, 페이지 바는 3 4 5 6 7 이 보이도록 함
     *  -> 현재 페이지가 중앙에 올 수 없는 경우이면 페이지 1부터 5개 표시(1 2 3 4 5)
     *  -> 현재 페이지(startNumber) + 바의 길이(BAR_LENGTH) 가 전체 페이지 수(totalPages)보다 크면 안됨
     */
    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) {
        int startNumber = currentPageNumber - (BAR_LENGTH / 2) < 0 ? 0 : currentPageNumber - (BAR_LENGTH / 2);
        int endNumber = startNumber + BAR_LENGTH >= totalPages ? totalPages : startNumber + BAR_LENGTH;

        return IntStream.range(startNumber, endNumber).boxed().toList();
    }

    public int currentBarLength() {
        return BAR_LENGTH;
    }
}
