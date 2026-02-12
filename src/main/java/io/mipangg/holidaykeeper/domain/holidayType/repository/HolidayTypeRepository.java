package io.mipangg.holidaykeeper.domain.holidayType.repository;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayType.entity.HolidayType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayTypeRepository extends JpaRepository<HolidayType, Long> {

    @Query("select ht from HolidayType ht where ht.holiday in :holidays")
    List<HolidayType> findByHolidayIn(@Param("holidays") List<Holiday> holidays);
}
