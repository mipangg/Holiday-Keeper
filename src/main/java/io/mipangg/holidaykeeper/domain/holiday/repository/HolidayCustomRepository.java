package io.mipangg.holidaykeeper.domain.holiday.repository;


import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayCustomRepository {

    Page<Holiday> searchHoliday(
            Integer year,
            String countryCode,
            String type,
            Integer from,
            Integer to,
            Pageable pageable
    );

}
