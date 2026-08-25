package com.munglog.service;

import com.munglog.dto.DogRequest;
import com.munglog.dto.DogResponse;
import com.munglog.entity.Dog;
import com.munglog.entity.Member;
import com.munglog.repository.DogRepository;
import com.munglog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DogService {

    private final DogRepository dogRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public DogResponse registerDog(DogRequest dogRequest, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        Dog dog = dogRequest.toEntity(member);
        Dog savedDog = dogRepository.save(dog);

        return new DogResponse(savedDog);
    }
}
