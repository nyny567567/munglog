package com.munglog.repository;

import com.munglog.entity.DiaryPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryPageRepository extends JpaRepository<DiaryPage, Long> {
}
