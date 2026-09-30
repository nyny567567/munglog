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
public class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("지역 코드로 피드 조회 요청 시 성공한다")
    void getFeeds_success() throws Exception {
        mockMvc.perform(
                        get("/api/feeds")
                                .param("regionCode", "SAMSONG")
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("지역 코드 없이 피드 조회 요청 시 실패한다")
    void getFeeds_fails_whenRegionCodeIsMissing() throws Exception {
        mockMvc.perform(
                        get("/api/feeds")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("지역 코드를 공백으로 피드 조회 요청 시 실패한다")
    void getFeeds_fails_whenRegionCodeIsBlank() throws Exception {
        mockMvc.perform(
                        get("/api/feeds")
                                .param("regionCode", " ")
                )
                .andExpect(status().isBadRequest());
    }




}
