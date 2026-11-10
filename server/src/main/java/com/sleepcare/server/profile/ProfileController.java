package com.sleepcare.server.profile;

import com.sleepcare.server.common.owner.OwnerIdInterceptor;
import com.sleepcare.server.profile.dto.Preset;
import com.sleepcare.server.profile.dto.ProfileCreateRequest;
import com.sleepcare.server.profile.dto.ProfileListResponse;
import com.sleepcare.server.profile.dto.ProfileNameRequest;
import com.sleepcare.server.profile.dto.ProfileResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> create(
            @RequestAttribute(OwnerIdInterceptor.ATTRIBUTE) String ownerId,
            @Valid @RequestBody ProfileCreateRequest request) {
        ProfileResponse response = profileService.create(ownerId, request);
        return ResponseEntity.created(URI.create("/api/v1/profiles/" + response.profileId())).body(response);
    }

    @GetMapping
    public ProfileListResponse list(@RequestAttribute(OwnerIdInterceptor.ATTRIBUTE) String ownerId) {
        return profileService.list(ownerId);
    }

    @GetMapping("/{profileId}")
    public ProfileResponse get(
            @RequestAttribute(OwnerIdInterceptor.ATTRIBUTE) String ownerId,
            @PathVariable Long profileId) {
        return profileService.get(ownerId, profileId);
    }

    @PatchMapping("/{profileId}")
    public ProfileResponse rename(
            @RequestAttribute(OwnerIdInterceptor.ATTRIBUTE) String ownerId,
            @PathVariable Long profileId,
            @Valid @RequestBody ProfileNameRequest request) {
        return profileService.rename(ownerId, profileId, request);
    }

    @PutMapping("/{profileId}/preset")
    public ProfileResponse updatePreset(
            @RequestAttribute(OwnerIdInterceptor.ATTRIBUTE) String ownerId,
            @PathVariable Long profileId,
            @Valid @RequestBody Preset preset) {
        return profileService.updatePreset(ownerId, profileId, preset);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> delete(
            @RequestAttribute(OwnerIdInterceptor.ATTRIBUTE) String ownerId,
            @PathVariable Long profileId) {
        profileService.delete(ownerId, profileId);
        return ResponseEntity.noContent().build();
    }
}
