package com.munglog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String regionCode;

    @Builder
    public Member(
            String email,
            String password,
            String nickname,
            String regionCode
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.regionCode = regionCode;
    }
}
