package com.fc.notice_board.notice_board.repository;

import com.fc.notice_board.notice_board.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
}
