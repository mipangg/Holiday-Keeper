package io.mipangg.holidaykeeper.util;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
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

}
