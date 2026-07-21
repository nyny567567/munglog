package com.munglog.dto;

import com.munglog.entity.Member;

public record SignupRequest(String email, String password, String nickname) {
    public Member toEntity() {
        return Member.builder()
                .email(this.email())
                .password(this.password())
                .nickname(this.nickname())
                .build();
    }
}
