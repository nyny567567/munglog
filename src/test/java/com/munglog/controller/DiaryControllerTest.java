package com.munglog.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("페이지 번호가 음수이면 요청에 실패한다")
    void getDiariesByDogId_fails_whenPageIsNegative() throws Exception {
        mockMvc.perform(
                get("/api/diaries")
                        .param("page", "-1")
                        .param("dogId", "1")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("페이지 크기가 0이면 요청에 실패한다")
    void getDiariesByDogId_fails_whenSizeIsZero() throws Exception {
        mockMvc.perform(
                get("/api/diaries")
                        .param("size", "0")
                        .param("dogId", "1")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("페이지 사이즈가 50을 초과하면 요청에 실패한다")
    void getDiariesByDogId_fails_whenSizeGreaterThan50() throws Exception {
        mockMvc.perform(
                get("/api/diaries")
                        .param("size", "51")
                        .param("dogId", "1")
        ).andExpect(status().isBadRequest());
    }
}
