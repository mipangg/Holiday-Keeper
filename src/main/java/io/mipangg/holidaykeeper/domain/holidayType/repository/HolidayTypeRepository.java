package io.mipangg.holidaykeeper.domain.holidayType.repository;

import io.mipangg.holidaykeeper.domain.holidayType.entity.HolidayType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayTypeRepository extends JpaRepository<HolidayType, Long> {

    Optional<HolidayType> findByTypeAndHoliday_Id(String type, Long holidayId);
}
