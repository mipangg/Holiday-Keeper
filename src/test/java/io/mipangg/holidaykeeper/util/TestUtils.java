package io.mipangg.holidaykeeper.util;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.time.LocalDate;

public class TestUtils {

    public static Holiday getHoliday() {
        return Holiday.builder()
                .id(1L)
                .date(LocalDate.parse("2025-01-01"))
                .localName("새해")
                .name("New Year's Day")
                .country(
                        Country.builder()
                                .name("South Korea")
                                .code("KR")
                                .build()
                )
                .isFixed(false)
                .isGlobal(true)
                .launchYear(null)
                .build();
    }

}
