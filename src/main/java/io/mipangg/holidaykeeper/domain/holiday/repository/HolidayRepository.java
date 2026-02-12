package io.mipangg.holidaykeeper.domain.holiday.repository;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    boolean existsBy();

    @Query("select h from Holiday h "
            + "where function('year', h.date) = :year and h.country.countryCode = :countryCode")
    List<Holiday> findByYearAndCountryCode(
            @Param("year") Integer year,
            @Param("countryCode") String countryCode
    );
}
