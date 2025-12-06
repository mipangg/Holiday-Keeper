package io.mipangg.holidaykeeper.domain.holidayType.repository;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayType.entity.HolidayType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayTypeRepository extends JpaRepository<HolidayType, Long> {

    Optional<HolidayType> findByTypeAndHoliday_Id(String type, Long holidayId);

    @Modifying
    @Query("delete from HolidayType hy where hy.holiday in :holidays")
    void deleteByHolidays(@Param("holidays") List<Holiday> holidays);
}
