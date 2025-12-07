package io.mipangg.holidaykeeper.util;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, Country> getCountryMap() {
        Map<String, Country> countries = new HashMap<>();
        getCountries().forEach(c -> {
            countries.put(c.getCode(), c);
        });
        return countries;
    }

    public static Country getCountryCanada() {
        return Country.builder()
                .code("CA")
                .name("Canada")
                .build();
    }

    public static List<String> getCountyStrsCanada() {
        return List.of(
                "CA-AB",
                "CA-BC",
                "CA-NB",
                "CA-ON",
                "CA-SK"
        );
    }

    public static List<County> getCountiesCanada() {
        Country canada = getCountryCanada();
        return List.of(
                County.builder()
                        .id(1L)
                        .country(canada)
                        .name("CA-AB")
                        .build(),
                County.builder()
                        .id(2L)
                        .country(canada)
                        .name("CA-BC")
                        .build(),
                County.builder()
                        .id(3L)
                        .country(canada)
                        .name("CA-NB")
                        .build(),
                County.builder()
                        .id(4L)
                        .country(canada)
                        .name("CA-ON")
                        .build(),
                County.builder()
                        .id(5L)
                        .country(canada)
                        .name("CA-SK")
                        .build()
        );
    }

    public static Holiday getHolidayCanada() {
        return Holiday.builder()
                .id(2L)
                .date(LocalDate.parse("2025-02-17"))
                .localName("Family Day")
                .name("Family Day")
                .country(getCountryCanada())
                .isFixed(false)
                .isGlobal(false)
                .launchYear(null)
                .build();
    }

    public static List<ExternalHolidayResponse> getExternalHolidayResponses() {
        return List.of(
                new ExternalHolidayResponse(
                        "2025-02-17",
                        "Family Day",
                        "Family Day",
                        "CA",
                        false,
                        false,
                        List.of(
                                "CA-AB",
                                "CA-BC",
                                "CA-NB",
                                "CA-ON",
                                "CA-SK"
                        ),
                        null,
                        List.of("Public")
                ));
    }
}
