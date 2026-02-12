package io.mipangg.holidaykeeper.domain.holidayCounty.repository;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayCounty.entity.HolidayCounty;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayCountyRepository extends JpaRepository<HolidayCounty, Long> {

    @Query("select hc from HolidayCounty hc where hc.holiday in :holidays")
    List<HolidayCounty> findByHolidayIn(@Param("holidays") List<Holiday> holidays);
}
