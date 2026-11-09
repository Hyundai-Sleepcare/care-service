package com.sleepcare.server.seat;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRecentProfileRepository extends JpaRepository<SeatRecentProfile, SeatRecentProfileId> {

    List<SeatRecentProfile> findAllByOwnerIdOrderByAppliedAtDesc(String ownerId);

    Optional<SeatRecentProfile> findByOwnerIdAndSeat(String ownerId, String seat);

    void deleteAllByProfileId(Long profileId);
}
