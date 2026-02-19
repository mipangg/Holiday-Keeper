package io.mipangg.holidaykeeper.domain.holidayCounty.repository;

import io.mipangg.holidaykeeper.domain.holidayCounty.entity.HolidayCounty;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayCountyRepository extends JpaRepository<HolidayCounty, Long> {

    @Query("select hc from HolidayCounty hc "
            + "join fetch hc.county where hc.holiday.id in :holidayIds")
    List<HolidayCounty> findByHolidayIdIn(@Param("holidayIds") List<Long> holidayIds);

}
