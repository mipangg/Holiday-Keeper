package io.mipangg.holidaykeeper.util;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;

public class TestUtil {

    public static Country getCountryBrazil() {
        return Country.builder()
                .name("Brazil")
                .countryCode("BR")
                .build();
    }

    public static Country getCountryCanada() {
        return Country.builder()
                .name("Canada")
                .countryCode("CA")
                .build();
    }

    public static Country getCountryKorea() {
        return Country.builder()
                .name("South Korea")
                .countryCode("KR")
                .build();
    }

    public static List<Country> getCountries() {
        return List.of(getCountryBrazil(), getCountryCanada(), getCountryKorea());
    }

    public static Holiday getHolidayBrazil() {
        return Holiday.builder()
                .date(LocalDate.of(2026, 2, 16))
                .localName("Carnaval")
                .name("Carnival")
                .country(getCountryBrazil())
                .fixed(false)
                .global(true)
                .launchYear(null)
                .build();
    }

    public static Holiday getHolidayCanada() {
        return Holiday.builder()
                .date(LocalDate.of(2026, 4, 6))
                .localName("Easter Monday")
                .name("Easter Monday")
                .country(getCountryCanada())
                .fixed(false)
                .global(false)
                .launchYear(null)
                .build();
    }

    public static Holiday getHolidayKorea() {
        return Holiday.builder()
                .date(LocalDate.of(2026, 1, 1))
                .localName("새해")
                .name("New Year's Day")
                .country(getCountryKorea())
                .fixed(false)
                .global(true)
                .launchYear(null)
                .build();
    }

}
