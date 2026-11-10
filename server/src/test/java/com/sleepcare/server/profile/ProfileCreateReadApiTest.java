package com.sleepcare.server.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sleepcare.server.common.owner.Owner;
import com.sleepcare.server.common.owner.OwnerRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfileCreateReadApiTest {

    private static final String OWNER_A = "aaaaaaaa-1111-4aaa-8aaa-aaaaaaaaaaaa";
    private static final String OWNER_B = "bbbbbbbb-2222-4bbb-8bbb-bbbbbbbbbbbb";

    @Autowired MockMvc mockMvc;
    @Autowired ProfileRepository profileRepository;
    @Autowired OwnerRepository ownerRepository;

    private static String body(String name, String angle, String heat, String vent, String celsius, String mood) {
        return """
                {"name": %s, "preset": {
                  "backrest": {"angle": %s},
                  "seatHeat": {"level": %s},
                  "seatVent": {"level": %s},
                  "temperature": {"targetCelsius": %s},
                  "ambientLight": {"mood": %s}
                }}""".formatted(name, angle, heat, vent, celsius, mood);
    }

    private static String validBody(String name) {
        return body("\"" + name + "\"", "128", "2", "0", "23.5", "\"AMBER\"");
    }

    private ResultActions create(String owner, String json) throws Exception {
        return mockMvc.perform(post("/api/v1/profiles")
                .header("X-Owner-Id", owner)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private ResultActions expectFieldError(ResultActions result, String field) throws Exception {
        return result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '" + field + "')]").exists());
    }

    private long createAndGetId(String owner, String name) throws Exception {
        MvcResult result = create(owner, validBody(name)).andExpect(status().isCreated()).andReturn();
        String location = result.getResponse().getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }

    // --- 생성 ---

    @Test
    void 프로필을_만들면_201과_Location과_전체_필드를_돌려준다() throws Exception {
        MvcResult result = create(OWNER_A, validBody("엄마"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern("/api/v1/profiles/\\d+")))
                .andExpect(jsonPath("$.profileId").isNumber())
                .andExpect(jsonPath("$.name").value("엄마"))
                .andExpect(jsonPath("$.preset.backrest.angle").value(128))
                .andExpect(jsonPath("$.preset.seatHeat.level").value(2))
                .andExpect(jsonPath("$.preset.seatVent.level").value(0))
                .andExpect(jsonPath("$.preset.temperature.targetCelsius").value(23.5))
                .andExpect(jsonPath("$.preset.ambientLight.mood").value("AMBER"))
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.updatedAt").isString())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        String createdAt = com.jayway.jsonpath.JsonPath.read(json, "$.createdAt");
        String updatedAt = com.jayway.jsonpath.JsonPath.read(json, "$.updatedAt");
        assertThat(createdAt).isEqualTo(updatedAt).endsWith("Z");
    }

    @Test
    void 이름_앞뒤_공백은_지우고_저장한다() throws Exception {
        create(OWNER_A, validBody("  엄마  "))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("엄마"));
    }

    @Test
    void 이름은_공백_포함_20자를_넘어도_지운_뒤_20자면_통과() throws Exception {
        create(OWNER_A, validBody("  " + "가".repeat(20) + "  "))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"\"", "\"   \"", "null"})
    void 이름이_비면_400(String name) throws Exception {
        expectFieldError(create(OWNER_A, body(name, "128", "2", "0", "23.5", "\"AMBER\"")), "name");
    }

    @Test
    void 이름이_21자면_400() throws Exception {
        expectFieldError(create(OWNER_A, validBody("가".repeat(21))), "name");
    }

    @Test
    void 프리셋이_없으면_400() throws Exception {
        expectFieldError(create(OWNER_A, "{\"name\": \"엄마\"}"), "preset");
    }

    @Test
    void 프리셋_안의_항목이_없으면_400() throws Exception {
        String json = """
                {"name": "엄마", "preset": {
                  "backrest": {"angle": 128},
                  "seatVent": {"level": 0},
                  "temperature": {},
                  "ambientLight": {"mood": "AMBER"}
                }}""";
        ResultActions result = create(OWNER_A, json);
        expectFieldError(result, "preset.seatHeat");
        result.andExpect(jsonPath("$.fieldErrors[?(@.field == 'preset.temperature.targetCelsius')]").exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "3"})
    void 단계_0과_3은_통과(String level) throws Exception {
        create(OWNER_A, body("\"엄마\"", "128", level, "0", "23.5", "\"AMBER\"")).andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "4"})
    void 단계_범위를_벗어나면_400(String level) throws Exception {
        expectFieldError(create(OWNER_A, body("\"엄마\"", "128", level, "0", "23.5", "\"AMBER\"")),
                "preset.seatHeat.level");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2.5", "\"2\""})
    void 단계가_정수가_아니면_400(String level) throws Exception {
        expectFieldError(create(OWNER_A, body("\"엄마\"", "128", level, "0", "23.5", "\"AMBER\"")),
                "preset.seatHeat.level");
    }

    @ParameterizedTest
    @ValueSource(strings = {"100", "145", "143"})
    void 각도_100_145_홀수는_통과(String angle) throws Exception {
        create(OWNER_A, body("\"엄마\"", angle, "2", "0", "23.5", "\"AMBER\"")).andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"99", "146"})
    void 각도_범위를_벗어나면_400(String angle) throws Exception {
        expectFieldError(create(OWNER_A, body("\"엄마\"", angle, "2", "0", "23.5", "\"AMBER\"")),
                "preset.backrest.angle");
    }

    @ParameterizedTest
    @ValueSource(strings = {"16.0", "32.0", "23.5", "24"})
    void 온도_범위_안_0_5_단위는_통과(String celsius) throws Exception {
        create(OWNER_A, body("\"엄마\"", "128", "2", "0", celsius, "\"AMBER\"")).andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"15.5", "32.5", "23.3", "\"23.0\""})
    void 온도가_틀리면_400(String celsius) throws Exception {
        expectFieldError(create(OWNER_A, body("\"엄마\"", "128", "2", "0", celsius, "\"AMBER\"")),
                "preset.temperature.targetCelsius");
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"AMBER\"", "\"SOFT_BLUE\"", "\"LAVENDER\"", "\"OFF\""})
    void 무드_4개_값은_통과(String mood) throws Exception {
        create(OWNER_A, body("\"엄마\"", "128", "2", "0", "23.5", mood)).andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"RED\"", "\"amber\"", "null"})
    void 무드가_틀리면_400(String mood) throws Exception {
        expectFieldError(create(OWNER_A, body("\"엄마\"", "128", "2", "0", "23.5", mood)),
                "preset.ambientLight.mood");
    }

    @Test
    void 열선과_통풍을_동시에_켜면_400() throws Exception {
        expectFieldError(create(OWNER_A, body("\"엄마\"", "128", "1", "2", "23.5", "\"AMBER\"")),
                "preset.seatVent.level");
    }

    @Test
    void 통풍만_켜면_통과() throws Exception {
        create(OWNER_A, body("\"엄마\"", "128", "0", "2", "23.5", "\"AMBER\""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.preset.seatVent.level").value(2));
    }

    @Test
    void 온도는_소수_한_자리로_저장된다() throws Exception {
        create(OWNER_A, body("\"엄마\"", "128", "2", "0", "23.50", "\"AMBER\""))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"targetCelsius\":23.5}")));
        create(OWNER_A, body("\"아빠\"", "128", "2", "0", "24", "\"AMBER\""))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"targetCelsius\":24.0}")));
    }

    @Test
    void 에러_사유는_한국어로_나온다() throws Exception {
        create(OWNER_A, body("\"엄마\"", "99", "2", "0", "23.3", "\"AMBER\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'preset.backrest.angle')].reason")
                        .value("100~145 사이 정수여야 합니다."))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'preset.temperature.targetCelsius')].reason")
                        .value("16.0~32.0 사이(0.5 단위)여야 합니다."));
        create(OWNER_A, validBody(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("이름은 앞뒤 공백을 빼고 1~20자여야 합니다."));
    }

    @Test
    void 열한번째_프로필은_409() throws Exception {
        saveTenProfiles(OWNER_A);
        create(OWNER_A, validBody("열한번째"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PROFILE_LIMIT_EXCEEDED"))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void 열한번째인데_입력도_틀리면_400이_먼저() throws Exception {
        saveTenProfiles(OWNER_A);
        expectFieldError(create(OWNER_A, validBody("")), "name");
    }

    @Test
    void 헤더_없이_만들면_401() throws Exception {
        mockMvc.perform(post("/api/v1/profiles").contentType(MediaType.APPLICATION_JSON).content(validBody("엄마")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("OWNER_ID_REQUIRED"));
    }

    private void saveTenProfiles(String owner) {
        if (!ownerRepository.existsById(owner)) {
            ownerRepository.save(new Owner(owner, Instant.now()));
        }
        for (int i = 1; i <= 10; i++) {
            profileRepository.save(new Profile(owner, "프로필" + i, 128, 0, 0,
                    new BigDecimal("23.0"), "OFF", Instant.now()));
        }
    }

    // --- 목록 ---

    @Test
    void 프로필이_없으면_빈_목록() throws Exception {
        mockMvc.perform(get("/api/v1/profiles").header("X-Owner-Id", OWNER_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profiles").isArray())
                .andExpect(jsonPath("$.profiles").isEmpty());
    }

    @Test
    void 목록은_생성_순서이고_다른_사용자_것은_안_보인다() throws Exception {
        createAndGetId(OWNER_A, "엄마");
        createAndGetId(OWNER_A, "아빠");
        createAndGetId(OWNER_B, "다른집");

        mockMvc.perform(get("/api/v1/profiles").header("X-Owner-Id", OWNER_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profiles", hasSize(2)))
                .andExpect(jsonPath("$.profiles[0].name").value("엄마"))
                .andExpect(jsonPath("$.profiles[1].name").value("아빠"))
                .andExpect(jsonPath("$.profiles[0].preset.temperature.targetCelsius").value(23.5));
    }

    @Test
    void 헤더_없이_목록을_보면_401() throws Exception {
        mockMvc.perform(get("/api/v1/profiles")).andExpect(status().isUnauthorized());
    }

    // --- 단건 ---

    @Test
    void 내_프로필_하나를_조회한다() throws Exception {
        long id = createAndGetId(OWNER_A, "엄마");
        mockMvc.perform(get("/api/v1/profiles/" + id).header("X-Owner-Id", OWNER_A))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value(id))
                .andExpect(jsonPath("$.name").value("엄마"))
                .andExpect(jsonPath("$.preset.ambientLight.mood").value("AMBER"));
    }

    @Test
    void 없는_프로필은_404() throws Exception {
        mockMvc.perform(get("/api/v1/profiles/999999").header("X-Owner-Id", OWNER_A))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROFILE_NOT_FOUND"));
    }

    @Test
    void 다른_사용자의_프로필은_404() throws Exception {
        long id = createAndGetId(OWNER_A, "엄마");
        mockMvc.perform(get("/api/v1/profiles/" + id).header("X-Owner-Id", OWNER_B))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROFILE_NOT_FOUND"));
    }

    @Test
    void 프로필_ID가_숫자가_아니면_400() throws Exception {
        mockMvc.perform(get("/api/v1/profiles/abc").header("X-Owner-Id", OWNER_A))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("profileId"));
    }
}
