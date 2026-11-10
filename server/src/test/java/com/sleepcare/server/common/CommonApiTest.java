package com.sleepcare.server.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sleepcare.server.common.owner.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class CommonApiTest {

    private static final String OWNER = "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa";

    @Autowired MockMvc mockMvc;
    @Autowired OwnerRepository ownerRepository;

    private ResultActions postEcho(String json) throws Exception {
        return mockMvc.perform(post("/api/v1/test/echo")
                .header("X-Owner-Id", OWNER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    @Test
    void 헤더가_없으면_401() throws Exception {
        mockMvc.perform(get("/api/v1/test/owner"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("OWNER_ID_REQUIRED"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1-1-1-1-1", "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaaa"})
    void UUID_형식이_아니면_401(String ownerId) throws Exception {
        mockMvc.perform(get("/api/v1/test/owner").header("X-Owner-Id", ownerId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("OWNER_ID_REQUIRED"));
    }

    @Test
    void 대문자_UUID는_소문자로_통과한다() throws Exception {
        mockMvc.perform(get("/api/v1/test/owner").header("X-Owner-Id", "BBBBBBBB-BBBB-4BBB-8BBB-BBBBBBBBBBBB"))
                .andExpect(status().isOk())
                .andExpect(content().string("bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb"));
    }

    @Test
    void 처음_보는_사용자는_한_번만_만들어진다() throws Exception {
        String newOwner = "cccccccc-cccc-4ccc-8ccc-cccccccccccc";
        assertThat(ownerRepository.existsById(newOwner)).isFalse();
        long before = ownerRepository.count();

        mockMvc.perform(get("/api/v1/test/owner").header("X-Owner-Id", newOwner)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/test/owner").header("X-Owner-Id", newOwner)).andExpect(status().isOk());

        assertThat(ownerRepository.existsById(newOwner)).isTrue();
        assertThat(ownerRepository.count()).isEqualTo(before + 1);
    }

    @Test
    void 정상_요청은_그대로_통과한다() throws Exception {
        postEcho("{\"name\":\"엄마\",\"inner\":{\"level\":2}}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inner.level").value(2));
    }

    @Test
    void 검증_실패는_400과_필드_경로를_돌려준다() throws Exception {
        postEcho("{\"name\":\"\",\"inner\":{}}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'name')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'inner.level')]").exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2.5", "\"2\"", "true", "\"abc\""})
    void 숫자_자리에_다른_타입이_오면_400(String level) throws Exception {
        postEcho("{\"name\":\"엄마\",\"inner\":{\"level\":" + level + "}}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("inner.level"));
    }

    @Test
    void JSON_형식이_깨지면_400() throws Exception {
        postEcho("{\"name\":")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void 본문이_없으면_400() throws Exception {
        mockMvc.perform(post("/api/v1/test/echo").header("X-Owner-Id", OWNER)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void Content_Type이_JSON이_아니면_400() throws Exception {
        mockMvc.perform(post("/api/v1/test/echo").header("X-Owner-Id", OWNER)
                        .contentType(MediaType.TEXT_PLAIN).content("hello"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void 경로_변수_타입이_틀리면_400() throws Exception {
        mockMvc.perform(get("/api/v1/test/items/abc").header("X-Owner-Id", OWNER))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("itemId"));
    }

    @Test
    void 헤더_검사가_입력_검사보다_먼저다() throws Exception {
        mockMvc.perform(post("/api/v1/test/echo").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 처리하지_못한_예외는_500() throws Exception {
        mockMvc.perform(get("/api/v1/test/boom").header("X-Owner-Id", OWNER))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void 없는_경로는_404_그대로다() throws Exception {
        mockMvc.perform(get("/api/v1/nothing").header("X-Owner-Id", OWNER))
                .andExpect(status().isNotFound());
    }

    @Test
    void 없는_경로도_헤더가_없으면_401() throws Exception {
        mockMvc.perform(get("/api/v1/profiles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("OWNER_ID_REQUIRED"));
    }

    @Test
    void 스웨거_문서는_헤더_없이_열린다() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.X-Owner-Id.in").value("header"));
    }
}
