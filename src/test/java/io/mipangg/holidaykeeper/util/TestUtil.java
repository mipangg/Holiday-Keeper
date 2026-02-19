package io.mipangg.holidaykeeper.util;

import io.mipangg.holidaykeeper.domain.common.PageResponse;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayListReadResponse;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;

public class TestUtil {

    public static Country genCountryBrazil() {
        return Country.builder()
                .name("Brazil")
                .countryCode("BR")
                .build();
    }

    public static Country genCountryCanada() {
        return Country.builder()
                .name("Canada")
                .countryCode("CA")
                .build();
    }

    public static Country genCountryKorea() {
        return Country.builder()
                .name("South Korea")
                .countryCode("KR")
                .build();
    }

    public static List<Country> genCountries() {
        return List.of(genCountryBrazil(), genCountryCanada(), genCountryKorea());
    }

    public static Holiday genHolidayBrazil() {
        return Holiday.builder()
                .date(LocalDate.of(2026, 2, 16))
                .localName("Carnaval")
                .name("Carnival")
                .country(genCountryBrazil())
                .fixed(false)
                .global(true)
                .launchYear(null)
                .build();
    }

    public static Holiday genHolidayCanada() {
        return Holiday.builder()
                .date(LocalDate.of(2026, 4, 6))
                .localName("Easter Monday")
                .name("Easter Monday")
                .country(genCountryCanada())
                .fixed(false)
                .global(false)
                .launchYear(null)
                .build();
    }

    public static Holiday genHolidayKorea() {
        return Holiday.builder()
                .date(LocalDate.of(2026, 1, 1))
                .localName("새해")
                .name("New Year's Day")
                .country(genCountryKorea())
                .fixed(false)
                .global(true)
                .launchYear(null)
                .build();
    }

    public static List<HolidayListReadResponse> genHolidayListReadResponses() {
        return List.of(
                new HolidayListReadResponse(
                        1L,
                        LocalDate.of(2026, 1, 1),
                        "새해",
                        "New Year's Day",
                        "South Korea",
                        null,
                        List.of("Public")
                ),
                new HolidayListReadResponse(
                        2L,
                        LocalDate.of(2026, 2, 16),
                        "설날",
                        "Lunar New Year",
                        "South Korea",
                        null,
                        List.of("Public")
                )
        );
    }


}
