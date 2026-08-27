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

import java.util.List;

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

    //내 강아지 전체 목록 조회 API
    @Transactional(readOnly = true)
    public List<DogResponse> getMyDogs(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        return dogRepository.findByMember(member).stream()
                .map(DogResponse::new)
                .toList();
    }

    //단건 상세 조회 API
    @Transactional(readOnly = true)
    public DogResponse getDogById(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강아지를 찾을 수 없습니다."));

        return new DogResponse(dog);

    }

    @Transactional
    public DogResponse updateDog(Long id, DogRequest dogRequest, String loginEmail) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 강아지를 찾을 수 없습니다."));

        if (!dog.getMember().getEmail().equals(loginEmail)) {
            throw new IllegalArgumentException("자신의 강아지 정보만 수정할 수 있습니다.");
        }

        dog.updateDog(
                dogRequest.name(),
                dogRequest.breed(),
                dogRequest.birthDate(),
                dogRequest.familyDate(),
                dogRequest.gender(),
                dogRequest.neutered(),
                dogRequest.description(),
                dogRequest.profileImageUrl()
        );

        return new DogResponse(dog);
    }

    @Transactional
    public void deleteDog(Long id, String loginEmail) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 강아지를 찾을 수 없습니다."));

        if (!dog.getMember().getEmail().equals(loginEmail)) {
            throw new IllegalArgumentException("자신의 강아지 정보만 삭제할 수 있습니다.");
        }

        dogRepository.delete(dog);
    }
}
