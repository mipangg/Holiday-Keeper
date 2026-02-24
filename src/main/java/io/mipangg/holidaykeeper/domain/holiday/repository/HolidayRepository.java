package io.mipangg.holidaykeeper.domain.holiday.repository;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayCustomRepository {

    boolean existsBy();

    @Query("select h from Holiday h "
            + "where h.date between :start and :end and h.country.countryCode = :countryCode")
    List<Holiday> findByYearAndCountryCode(
            @Param(":start") LocalDate start,
            @Param(":end") LocalDate end,
            @Param("countryCode") String countryCode
    );

}
