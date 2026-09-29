package com.munglog.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("지역 코드가 공백이면 회원가입에 실패한다")
    void signup_fails_whenRegionCodeIsBlank() throws Exception {
        String requestBody = """
                {
                        "email" : "testMember@munglog.com",
                        "password" : "1234",
                        "nickname" : "토토누나",
                        "regionCode" : "    "
                
                    }
                """;

        mockMvc.perform(
                post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        ).andExpect(status().isBadRequest());
    }


}
