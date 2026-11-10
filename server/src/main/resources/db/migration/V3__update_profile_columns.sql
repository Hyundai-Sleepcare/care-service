ALTER TABLE profile DROP COLUMN avatar;
ALTER TABLE profile DROP COLUMN seat_heat_enabled;
ALTER TABLE profile DROP COLUMN seat_vent_enabled;
ALTER TABLE profile DROP COLUMN backrest_enabled;
ALTER TABLE profile DROP COLUMN ambient_light_enabled;
ALTER TABLE profile ADD COLUMN temperature_celsius DECIMAL(3,1) NOT NULL;
ALTER TABLE profile ADD COLUMN ambient_light_mood VARCHAR(10) NOT NULL;
