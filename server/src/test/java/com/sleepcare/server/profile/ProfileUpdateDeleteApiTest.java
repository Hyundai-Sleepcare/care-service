package com.sleepcare.server.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfileUpdateDeleteApiTest {

    private static final String OWNER_A = "aaaaaaaa-3333-4aaa-8aaa-aaaaaaaaaaaa";
    private static final String OWNER_B = "bbbbbbbb-4444-4bbb-8bbb-bbbbbbbbbbbb";

    private static final String CREATE_BODY = """
            {"name": "엄마", "preset": {
              "backrest": {"angle": 128},
              "seatHeat": {"level": 2},
              "seatVent": {"level": 0},
              "temperature": {"targetCelsius": 23.0},
              "ambientLight": {"mood": "AMBER"}
            }}""";

    @Autowired MockMvc mockMvc;
    @Autowired ProfileRepository profileRepository;

    private static String preset(String angle, String heat, String vent, String celsius, String mood) {
        return """
                {
                  "backrest": {"angle": %s},
                  "seatHeat": {"level": %s},
                  "seatVent": {"level": %s},
                  "temperature": {"targetCelsius": %s},
                  "ambientLight": {"mood": %s}
                }""".formatted(angle, heat, vent, celsius, mood);
    }

    /** 프로필을 하나 만들고 응답 JSON을 돌려준다 */
    private String createProfile(String owner) throws Exception {
        return mockMvc.perform(post("/api/v1/profiles").header("X-Owner-Id", owner)
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_BODY))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private static long idOf(String json) {
        return ((Number) JsonPath.read(json, "$.profileId")).longValue();
    }

    private ResultActions patchName(String owner, long id, String json) throws Exception {
        return mockMvc.perform(patch("/api/v1/profiles/" + id).header("X-Owner-Id", owner)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions putPreset(String owner, long id, String json) throws Exception {
        return mockMvc.perform(put("/api/v1/profiles/" + id + "/preset").header("X-Owner-Id", owner)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions deleteProfile(String owner, long id) throws Exception {
        return mockMvc.perform(delete("/api/v1/profiles/" + id).header("X-Owner-Id", owner));
    }

    private static void waitAMoment() throws InterruptedException {
        Thread.sleep(5); // updatedAt이 createdAt과 확실히 달라지게
    }

    // --- 이름 변경 (PATCH) ---

    @Test
    void 이름만_바뀌고_프리셋과_생성_시각은_그대로다() throws Exception {
        String created = createProfile(OWNER_A);
        long id = idOf(created);
        waitAMoment();

        String renamed = patchName(OWNER_A, id, "{\"name\": \"  어머니  \"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value(id))
                .andExpect(jsonPath("$.name").value("어머니"))
                .andExpect(jsonPath("$.preset.backrest.angle").value(128))
                .andExpect(jsonPath("$.preset.seatHeat.level").value(2))
                .andExpect(jsonPath("$.preset.ambientLight.mood").value("AMBER"))
                .andReturn().getResponse().getContentAsString();

        String createdAt = JsonPath.read(created, "$.createdAt");
        assertThat((String) JsonPath.read(renamed, "$.createdAt")).isEqualTo(createdAt);
        assertThat((String) JsonPath.read(renamed, "$.updatedAt")).isNotEqualTo(createdAt);

        mockMvc.perform(get("/api/v1/profiles/" + id).header("X-Owner-Id", OWNER_A))
                .andExpect(jsonPath("$.name").value("어머니"));
    }

    @Test
    void 같은_이름으로_바꿔도_updatedAt은_갱신된다() throws Exception {
        String created = createProfile(OWNER_A);
        waitAMoment();
        String renamed = patchName(OWNER_A, idOf(created), "{\"name\": \"엄마\"}")
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat((String) JsonPath.read(renamed, "$.updatedAt"))
                .isNotEqualTo(JsonPath.read(created, "$.updatedAt"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"name\": \"\"}", "{\"name\": \"   \"}", "{\"name\": null}"})
    void 이름이_비면_400(String json) throws Exception {
        long id = idOf(createProfile(OWNER_A));
        patchName(OWNER_A, id, json)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"))
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("이름은 앞뒤 공백을 빼고 1~20자여야 합니다."));
    }

    @Test
    void 이름이_21자면_400() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        patchName(OWNER_A, id, "{\"name\": \"" + "가".repeat(21) + "\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }

    @Test
    void 다른_사용자의_프로필_이름은_못_바꾼다() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        patchName(OWNER_B, id, "{\"name\": \"남의것\"}")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROFILE_NOT_FOUND"));
        mockMvc.perform(get("/api/v1/profiles/" + id).header("X-Owner-Id", OWNER_A))
                .andExpect(jsonPath("$.name").value("엄마"));
    }

    @Test
    void 없는_프로필_이름_변경은_404() throws Exception {
        patchName(OWNER_A, 999999, "{\"name\": \"없음\"}")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROFILE_NOT_FOUND"));
    }

    // --- 프리셋 수정 (PUT) ---

    @Test
    void 프리셋이_통째로_바뀌고_이름과_생성_시각은_그대로다() throws Exception {
        String created = createProfile(OWNER_A);
        long id = idOf(created);
        waitAMoment();

        String updated = putPreset(OWNER_A, id, preset("130", "0", "3", "23.5", "\"LAVENDER\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("엄마"))
                .andExpect(jsonPath("$.preset.backrest.angle").value(130))
                .andExpect(jsonPath("$.preset.seatHeat.level").value(0))
                .andExpect(jsonPath("$.preset.seatVent.level").value(3))
                .andExpect(jsonPath("$.preset.temperature.targetCelsius").value(23.5))
                .andExpect(jsonPath("$.preset.ambientLight.mood").value("LAVENDER"))
                .andReturn().getResponse().getContentAsString();

        String createdAt = JsonPath.read(created, "$.createdAt");
        assertThat((String) JsonPath.read(updated, "$.createdAt")).isEqualTo(createdAt);
        assertThat((String) JsonPath.read(updated, "$.updatedAt")).isNotEqualTo(createdAt);

        mockMvc.perform(get("/api/v1/profiles/" + id).header("X-Owner-Id", OWNER_A))
                .andExpect(jsonPath("$.preset.ambientLight.mood").value("LAVENDER"))
                .andExpect(jsonPath("$.preset.seatVent.level").value(3));
    }

    @Test
    void 프리셋_온도도_소수_한_자리로_저장된다() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        putPreset(OWNER_A, id, preset("128", "2", "0", "24", "\"AMBER\""))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"targetCelsius\":24.0}")));
    }

    @Test
    void 프리셋_항목이_빠지면_400이고_필드에_preset이_붙지_않는다() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        String json = """
                {
                  "backrest": {"angle": 128},
                  "seatHeat": {},
                  "temperature": {"targetCelsius": 23.0},
                  "ambientLight": {"mood": "AMBER"}
                }""";
        putPreset(OWNER_A, id, json)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'seatHeat.level')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'seatVent')]").exists());
    }

    @Test
    void 프리셋_열선과_통풍을_동시에_켜면_400() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        putPreset(OWNER_A, id, preset("128", "1", "1", "23.0", "\"AMBER\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("seatVent.level"))
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("열선과 통풍은 동시에 켤 수 없습니다."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"99", "146"})
    void 프리셋_각도가_틀리면_400(String angle) throws Exception {
        long id = idOf(createProfile(OWNER_A));
        putPreset(OWNER_A, id, preset(angle, "2", "0", "23.0", "\"AMBER\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("backrest.angle"));
    }

    @Test
    void 프리셋_무드가_틀리면_400() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        putPreset(OWNER_A, id, preset("128", "2", "0", "23.0", "\"RED\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("ambientLight.mood"));
    }

    @Test
    void 프리셋_온도가_틀리면_400() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        putPreset(OWNER_A, id, preset("128", "2", "0", "23.3", "\"AMBER\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("temperature.targetCelsius"));
    }

    @Test
    void 다른_사용자의_프리셋은_못_바꾼다() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        putPreset(OWNER_B, id, preset("100", "0", "0", "16.0", "\"OFF\""))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROFILE_NOT_FOUND"));
        mockMvc.perform(get("/api/v1/profiles/" + id).header("X-Owner-Id", OWNER_A))
                .andExpect(jsonPath("$.preset.backrest.angle").value(128));
    }

    @Test
    void 없는_프로필_프리셋_수정은_404() throws Exception {
        putPreset(OWNER_A, 999999, preset("128", "2", "0", "23.0", "\"AMBER\""))
                .andExpect(status().isNotFound());
    }

    // --- 삭제 (DELETE) ---

    @Test
    void 삭제하면_204이고_본문이_없고_이후_조회는_404() throws Exception {
        long id = idOf(createProfile(OWNER_A));

        deleteProfile(OWNER_A, id)
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/v1/profiles/" + id).header("X-Owner-Id", OWNER_A))
                .andExpect(status().isNotFound());
        assertThat(profileRepository.findById(id)).isEmpty();
    }

    @Test
    void 이미_지운_프로필을_다시_지우면_404() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        deleteProfile(OWNER_A, id).andExpect(status().isNoContent());
        deleteProfile(OWNER_A, id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROFILE_NOT_FOUND"));
    }

    @Test
    void 다른_사용자의_프로필은_못_지운다() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        deleteProfile(OWNER_B, id).andExpect(status().isNotFound());
        assertThat(profileRepository.findById(id)).isPresent();
    }

    @Test
    void 하나_지우면_목록에서_빠지고_다시_만들_수_있다() throws Exception {
        long first = idOf(createProfile(OWNER_A));
        idOf(createProfile(OWNER_A));
        deleteProfile(OWNER_A, first).andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/profiles").header("X-Owner-Id", OWNER_A))
                .andExpect(jsonPath("$.profiles.length()").value(1));
    }

    // --- 공통 ---

    @Test
    void 헤더_없이_수정_삭제하면_401() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        mockMvc.perform(patch("/api/v1/profiles/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"a\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/profiles/" + id + "/preset").contentType(MediaType.APPLICATION_JSON)
                        .content(preset("128", "2", "0", "23.0", "\"AMBER\"")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/v1/profiles/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 프로필_ID가_숫자가_아니면_400() throws Exception {
        mockMvc.perform(delete("/api/v1/profiles/abc").header("X-Owner-Id", OWNER_A))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("profileId"));
    }

    @Test
    void 입력이_틀리면_남의_프로필이어도_400이_먼저() throws Exception {
        long id = idOf(createProfile(OWNER_A));
        patchName(OWNER_B, id, "{\"name\": \"\"}").andExpect(status().isBadRequest());
    }
}
