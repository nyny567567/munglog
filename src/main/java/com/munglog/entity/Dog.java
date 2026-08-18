package com.munglog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @Column(nullable = false)
    private String name;

    private String breed;

    private LocalDate birthDate;

    private LocalDate familyDate; //가족이 된 날

    private String gender;

    private Boolean neutered; // 중성화 여부

    @Column(columnDefinition = "TEXT")
    private String description;

    private String profileImageUrl;

    @Builder
    public Dog(Member member, String name, String breed, LocalDate birthDate, LocalDate familyDate, String gender, Boolean neutered, String description, String profileImageUrl) {
        this.member = member;
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.familyDate = familyDate;
        this.gender = gender;
        this.neutered = neutered;
        this.description = description;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateDog(String name, String breed, LocalDate birthDate, LocalDate familyDate, String gender, Boolean neutered, String description, String profileImageUrl) {
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.familyDate = familyDate;
        this.gender = gender;
        this.neutered = neutered;
        this.description = description;
        this.profileImageUrl = profileImageUrl;
    }
}
