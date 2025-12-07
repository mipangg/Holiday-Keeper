package io.mipangg.holidaykeeper.domain.holiday.repository;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayTypeRepository extends JpaRepository<HolidayType, Long> {

    Optional<HolidayType> findByTypeAndHoliday_Id(String type, Long holidayId);

    @Modifying
    @Query(
            value = "UPDATE holiday_type "
                    + "SET deleted = true "
                    + "WHERE holiday_id IN (:holidayIds)",
            nativeQuery = true
    )
    void deleteByHolidays(@Param("holidayIds") List<Long> holidayIds);

    @Query("select ht from HolidayType ht where ht.holiday in :holidays")
    List<HolidayType> findByHolidays(@Param("holidays") List<Holiday> holidays);

}
