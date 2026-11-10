package com.sleepcare.server.profile;

import com.sleepcare.server.common.error.ApiException;
import com.sleepcare.server.common.error.ErrorCode;
import com.sleepcare.server.profile.dto.Preset;
import com.sleepcare.server.profile.dto.ProfileCreateRequest;
import com.sleepcare.server.profile.dto.ProfileListResponse;
import com.sleepcare.server.profile.dto.ProfileNameRequest;
import com.sleepcare.server.profile.dto.ProfileResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProfileService {

    public static final int MAX_PROFILES = 10;

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public ProfileResponse create(String ownerId, ProfileCreateRequest request) {
        if (profileRepository.countByOwnerId(ownerId) >= MAX_PROFILES) {
            throw new ApiException(ErrorCode.PROFILE_LIMIT_EXCEEDED);
        }
        Preset preset = request.preset();
        Profile profile = new Profile(ownerId, request.name(),
                preset.backrest().angle(),
                preset.seatHeat().level(),
                preset.seatVent().level(),
                toOneDecimal(preset.temperature().targetCelsius()),
                preset.ambientLight().mood().name(),
                Instant.now());
        return ProfileResponse.from(profileRepository.save(profile));
    }

    public ProfileListResponse list(String ownerId) {
        return new ProfileListResponse(profileRepository.findAllByOwnerIdOrderByProfileIdAsc(ownerId).stream()
                .map(ProfileResponse::from)
                .toList());
    }

    public ProfileResponse get(String ownerId, Long profileId) {
        return ProfileResponse.from(findOwned(ownerId, profileId));
    }

    @Transactional
    public ProfileResponse rename(String ownerId, Long profileId, ProfileNameRequest request) {
        Profile profile = findOwned(ownerId, profileId);
        profile.rename(request.name(), Instant.now());
        return ProfileResponse.from(profile);
    }

    @Transactional
    public ProfileResponse updatePreset(String ownerId, Long profileId, Preset preset) {
        Profile profile = findOwned(ownerId, profileId);
        profile.updatePreset(
                preset.backrest().angle(),
                preset.seatHeat().level(),
                preset.seatVent().level(),
                toOneDecimal(preset.temperature().targetCelsius()),
                preset.ambientLight().mood().name(),
                Instant.now());
        return ProfileResponse.from(profile);
    }

    @Transactional
    public void delete(String ownerId, Long profileId) {
        profileRepository.delete(findOwned(ownerId, profileId));
    }

    // 없거나 다른 사용자의 프로필이면 404
    private Profile findOwned(String ownerId, Long profileId) {
        return profileRepository.findByProfileIdAndOwnerId(profileId, ownerId)
                .orElseThrow(() -> new ApiException(ErrorCode.PROFILE_NOT_FOUND));
    }

    // 23.50 → 23.5, 24 → 24.0. DB(DECIMAL(3,1))와 같은 모양으로 맞춘다. 0.5 단위 검사를 통과한 값이라 반올림은 일어나지 않는다
    private static BigDecimal toOneDecimal(BigDecimal celsius) {
        return celsius.setScale(1, RoundingMode.UNNECESSARY);
    }
}
