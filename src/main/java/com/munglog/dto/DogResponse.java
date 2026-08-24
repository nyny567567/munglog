package com.munglog.dto;

import com.munglog.entity.Dog;

import java.time.LocalDate;

public record DogResponse(
        Long id,
        String name,
        String breed,
        LocalDate birthDate,
        LocalDate familyDate,
        String gender,
        Boolean neutered,
        String discription,
        String profileImageUrl
) {
    public DogResponse(Dog dog) {
        this(
            dog.getId(),
            dog.getName(),
            dog.getBreed(),
            dog.getBirthDate(),
            dog.getFamilyDate(),
            dog.getGender(),
            dog.getNeutered(),
            dog.getDescription(),
            dog.getProfileImageUrl()
        );
    }
}
