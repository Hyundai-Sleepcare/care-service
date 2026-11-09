package com.sleepcare.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.sleepcare.server.common.owner.Owner;
import com.sleepcare.server.common.owner.OwnerRepository;
import com.sleepcare.server.profile.Profile;
import com.sleepcare.server.profile.ProfileRepository;
import com.sleepcare.server.seat.SeatRecentProfile;
import com.sleepcare.server.seat.SeatRecentProfileRepository;
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
    @Autowired SeatRecentProfileRepository seatRecentProfileRepository;

    private Profile saveProfile(String ownerId, String name) {
        return profileRepository.save(new Profile(ownerId, name, 1,
                true, 2, false, 1, true, 128, true, Instant.now()));
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
    void 프로필_ID로_좌석_기록을_지운다() {
        ownerRepository.save(new Owner(OWNER_A, Instant.now()));
        Profile mom = saveProfile(OWNER_A, "엄마");
        Profile dad = saveProfile(OWNER_A, "아빠");
        seatRecentProfileRepository.save(new SeatRecentProfile(OWNER_A, "ROW_1_RIGHT", mom.getProfileId(), Instant.now()));
        seatRecentProfileRepository.save(new SeatRecentProfile(OWNER_A, "ROW_2_LEFT", dad.getProfileId(), Instant.now()));

        seatRecentProfileRepository.deleteAllByProfileId(mom.getProfileId());

        assertThat(seatRecentProfileRepository.findAllByOwnerIdOrderByAppliedAtDesc(OWNER_A))
                .extracting(SeatRecentProfile::getSeat).containsExactly("ROW_2_LEFT");
        assertThat(seatRecentProfileRepository.findByOwnerIdAndSeat(OWNER_A, "ROW_1_RIGHT")).isEmpty();
    }
}
