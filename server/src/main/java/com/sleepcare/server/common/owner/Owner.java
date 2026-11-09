package com.sleepcare.server.common.owner;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "owner")
public class Owner {

    @Id
    private String ownerId;

    private Instant createdAt;

    protected Owner() {
    }

    public Owner(String ownerId, Instant createdAt) {
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
