package com.munglog.repository;

import com.munglog.entity.Diary;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByDogId(Long dogId, Sort sort);
}
