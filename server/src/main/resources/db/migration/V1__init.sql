CREATE TABLE owner (
    owner_id   VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (owner_id)
);

CREATE TABLE profile (
    profile_id            BIGINT      NOT NULL AUTO_INCREMENT,
    owner_id              VARCHAR(36) NOT NULL,
    name                  VARCHAR(20) NOT NULL,
    avatar                TINYINT     NOT NULL,
    seat_heat_enabled     BOOLEAN     NOT NULL,
    seat_heat_level       TINYINT     NOT NULL,
    seat_vent_enabled     BOOLEAN     NOT NULL,
    seat_vent_level       TINYINT     NOT NULL,
    backrest_enabled      BOOLEAN     NOT NULL,
    backrest_angle        SMALLINT    NOT NULL,
    ambient_light_enabled BOOLEAN     NOT NULL,
    created_at            TIMESTAMP(6) NOT NULL,
    updated_at            TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (profile_id),
    CONSTRAINT fk_profile_owner FOREIGN KEY (owner_id) REFERENCES owner (owner_id)
);

CREATE TABLE sleep_setting (
    owner_id            VARCHAR(36)  NOT NULL,
    temperature_enabled BOOLEAN      NOT NULL,
    temperature_celsius DECIMAL(3,1) NOT NULL,
    fan_mode            VARCHAR(10)  NOT NULL,
    created_at          TIMESTAMP(6)  NOT NULL,
    updated_at          TIMESTAMP(6)  NOT NULL,
    PRIMARY KEY (owner_id),
    CONSTRAINT fk_sleep_setting_owner FOREIGN KEY (owner_id) REFERENCES owner (owner_id)
);

CREATE TABLE seat_recent_profile (
    owner_id   VARCHAR(36) NOT NULL,
    seat       VARCHAR(20) NOT NULL,
    profile_id BIGINT      NOT NULL,
    applied_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (owner_id, seat),
    CONSTRAINT fk_seat_recent_profile_owner FOREIGN KEY (owner_id) REFERENCES owner (owner_id),
    CONSTRAINT fk_seat_recent_profile_profile FOREIGN KEY (profile_id) REFERENCES profile (profile_id) ON DELETE CASCADE
);
