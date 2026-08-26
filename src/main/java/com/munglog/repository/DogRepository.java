package com.munglog.repository;

import com.munglog.entity.Dog;
import com.munglog.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {
    List<Dog> findByMember(Member member);
}
