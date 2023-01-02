package com.fc.notice_board.notice_board.repository.querydsl;

import java.util.List;

public interface HashtagRepositoryCustom {

    List<String> findAllHashtagNames();
}
