package io.mipangg.holidaykeeper.domain.holiday.dto;

import java.time.LocalDate;
import java.util.List;

public record HolidayDetailResponse(
        LocalDate date,
        String localName,
        String name,
        String country,
        String countryCode,
        boolean isGlobal,
        List<String> counties,
        List<String> types
) {

}
