package com.example.kwordpocket.notice.repository;

import com.example.kwordpocket.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
