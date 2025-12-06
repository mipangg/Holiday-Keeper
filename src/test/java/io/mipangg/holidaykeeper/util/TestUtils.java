package io.mipangg.holidaykeeper.util;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;

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

    public static List<Country> getCountries() {
        return List.of(
                Country.builder()
                        .code("BR")
                        .name("Brazil")
                        .build(),
                Country.builder()
                        .code("CA")
                        .name("Canada")
                        .build(),
                Country.builder()
                        .code("KR")
                        .name("South Korea")
                        .build()
        );
    }

}
