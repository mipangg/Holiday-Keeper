package io.mipangg.holidaykeeper.util;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TestUtils {

    public static Map<String, Country> getCountryMap() {
        return Map.of(
                "BR",
                Country.builder()
                        .countryCode("BR")
                        .name("Brazil")
                        .build(),
                "CA",
                Country.builder()
                        .countryCode("CA")
                        .name("Canada")
                        .build(),
                "KR",
                Country.builder()
                        .countryCode("KR")
                        .name("South Korea")
                        .build()
        );
    }

    public static List<Integer> getLastFiveYears() {
        int thisYear = LocalDate.now().getYear();
        return List.of(thisYear, thisYear - 1, thisYear - 2, thisYear - 3, thisYear - 4);
    }

}
