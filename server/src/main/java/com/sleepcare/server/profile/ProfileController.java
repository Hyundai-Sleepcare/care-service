package com.sleepcare.server.profile;

import com.sleepcare.server.common.owner.OwnerIdInterceptor;
import com.sleepcare.server.profile.dto.ProfileCreateRequest;
import com.sleepcare.server.profile.dto.ProfileResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
