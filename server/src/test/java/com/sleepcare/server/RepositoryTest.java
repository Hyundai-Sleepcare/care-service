package com.sleepcare.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.sleepcare.server.common.owner.Owner;
import com.sleepcare.server.common.owner.OwnerRepository;
import com.sleepcare.server.profile.Profile;
import com.sleepcare.server.profile.ProfileRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RepositoryTest {

    private static final String OWNER_A = "11111111-1111-1111-1111-111111111111";
    private static final String OWNER_B = "22222222-2222-2222-2222-222222222222";

    @Autowired OwnerRepository ownerRepository;
    @Autowired ProfileRepository profileRepository;

    private Profile saveProfile(String ownerId, String name) {
        return profileRepository.save(new Profile(ownerId, name, 128,
                2, 0, new BigDecimal("23.5"), "AMBER", Instant.now()));
    }

    @Test
    void 사용자별로_프로필이_섞이지_않는다() {
        ownerRepository.save(new Owner(OWNER_A, Instant.now()));
        ownerRepository.save(new Owner(OWNER_B, Instant.now()));
        Profile mom = saveProfile(OWNER_A, "엄마");
        saveProfile(OWNER_A, "아빠");
        saveProfile(OWNER_B, "다른집");

        assertThat(profileRepository.findAllByOwnerIdOrderByProfileIdAsc(OWNER_A))
                .extracting(Profile::getName).containsExactly("엄마", "아빠");
        assertThat(profileRepository.countByOwnerId(OWNER_A)).isEqualTo(2);
        assertThat(profileRepository.findByProfileIdAndOwnerId(mom.getProfileId(), OWNER_B)).isEmpty();
    }

    @Test
    void 프리셋_값이_그대로_저장된다() {
        ownerRepository.save(new Owner(OWNER_A, Instant.now()));
        Profile saved = saveProfile(OWNER_A, "엄마");
        profileRepository.flush();

        Profile found = profileRepository.findById(saved.getProfileId()).orElseThrow();
        assertThat(found.getTemperatureCelsius()).isEqualByComparingTo("23.5");
        assertThat(found.getAmbientLightMood()).isEqualTo("AMBER");
        assertThat(found.getBackrestAngle()).isEqualTo(128);
    }
}
