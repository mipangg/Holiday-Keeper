package io.mipangg.holidaykeeper.domain.holiday.repository;

import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayTypeRepository extends JpaRepository<HolidayType, Long> {

}
