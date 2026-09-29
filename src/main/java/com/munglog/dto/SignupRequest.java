package com.munglog.dto;

import com.munglog.entity.Member;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        String email,
        String password,
        String nickname,
        @NotBlank String regionCode
) {
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .email(this.email())
                .password(encodedPassword)
                .nickname(this.nickname())
                .regionCode(this.regionCode())
                .build();
    }
}
