package io.mipangg.holidaykeeper.util;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayListReadResponse;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.type.entity.Type;
import java.time.LocalDate;
import java.util.List;

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
                .id(1L)
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
                .id(2L)
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
                .id(3L)
                .date(LocalDate.of(2026, 1, 1))
                .localName("새해")
                .name("New Year's Day")
                .country(genCountryKorea())
                .fixed(false)
                .global(true)
                .launchYear(null)
                .build();
    }

    public static Type genTypePublic() {
        return Type.builder()
                .type("Public")
                .build();
    }

    public static Type genTypeBank() {
        return Type.builder()
                .type("Bank")
                .build();
    }

    public static County genCountyAb() {
        return County.builder()
                .county("CA-AB")
                .country(genCountryCanada())
                .build();
    }

    public static County genCountyPe() {
        return County.builder()
                .county("CA-PE")
                .country(genCountryCanada())
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
