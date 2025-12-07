package io.mipangg.holidaykeeper.domain.holiday.repository;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    @Query("select h from Holiday h "
            + "where h.date = :date and h.country = :country "
            + "and h.name = :name and h.isGlobal = :isGlobal"
    )
    Optional<Holiday> findByDateAndCountryAndNameAndIsGlobal(
            @Param("date") LocalDate date,
            @Param("country") Country country,
            @Param("name") String name,
            @Param("isGlobal") boolean isGlobal
    );

    @Query("select h from Holiday h "
            + "where YEAR(h.date) = :year and h.country.code = :countryCode")
    List<Holiday> findByYearAndCountryCode(
            @Param("year") int year,
            @Param("countryCode")  String countryCode
    );
}
