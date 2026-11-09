package com.sleepcare.server.sleepsetting;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepSettingRepository extends JpaRepository<SleepSetting, String> {
}
