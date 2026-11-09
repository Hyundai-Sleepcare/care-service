package com.sleepcare.server.profile;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findAllByOwnerIdOrderByProfileIdAsc(String ownerId);

    Optional<Profile> findByProfileIdAndOwnerId(Long profileId, String ownerId);

    long countByOwnerId(String ownerId);
}
