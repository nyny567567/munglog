package com.munglog.dto;

import com.munglog.entity.Member;

public record SignupRequest(String email, String password, String nickname) {
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .email(this.email())
                .password(encodedPassword)
                .nickname(this.nickname())
                .build();
    }
}
