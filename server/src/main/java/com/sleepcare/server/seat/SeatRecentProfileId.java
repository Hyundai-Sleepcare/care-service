package com.sleepcare.server.seat;

import java.io.Serializable;
import java.util.Objects;

public class SeatRecentProfileId implements Serializable {

    private String ownerId;
    private String seat;

    protected SeatRecentProfileId() {
    }

    public SeatRecentProfileId(String ownerId, String seat) {
        this.ownerId = ownerId;
        this.seat = seat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatRecentProfileId other)) return false;
        return Objects.equals(ownerId, other.ownerId) && Objects.equals(seat, other.seat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, seat);
    }
}
