package com.munglog.dto;

import com.munglog.entity.Dog;
import com.munglog.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDate;

public record DogRequest(
        String name,
        String breed,
        LocalDate birthDate,
        LocalDate familyDate, //가족이 된 날
        String gender,
        Boolean neutered, // 중성화 여부
        String description,
        String profileImageUrl
) {
    public Dog toEntity(Member member) {
        return Dog.builder()
                .member(member)
                .name(this.name)
                .breed(this.breed)
                .birthDate(this.birthDate)
                .familyDate(this.familyDate)
                .gender(this.gender)
                .neutered(this.neutered)
                .description(this.description)
                .profileImageUrl(this.profileImageUrl)
                .build();
                


    }


}
