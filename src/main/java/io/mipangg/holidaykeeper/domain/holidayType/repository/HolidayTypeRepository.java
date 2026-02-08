package io.mipangg.holidaykeeper.domain.holidayType.repository;

import io.mipangg.holidaykeeper.domain.holidayType.entity.HolidayType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayTypeRepository extends JpaRepository<HolidayType, Long> {

}
