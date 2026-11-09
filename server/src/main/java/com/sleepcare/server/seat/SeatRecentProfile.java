package com.sleepcare.server.seat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "seat_recent_profile")
@IdClass(SeatRecentProfileId.class)
public class SeatRecentProfile {

    @Id
    private String ownerId;

    @Id
    private String seat;

    private Long profileId;

    private Instant appliedAt;

    protected SeatRecentProfile() {
    }

    public SeatRecentProfile(String ownerId, String seat, Long profileId, Instant appliedAt) {
        this.ownerId = ownerId;
        this.seat = seat;
        this.profileId = profileId;
        this.appliedAt = appliedAt;
    }

    public String getOwnerId() { return ownerId; }
    public String getSeat() { return seat; }
    public Long getProfileId() { return profileId; }
    public Instant getAppliedAt() { return appliedAt; }
}
