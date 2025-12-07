package io.mipangg.holidaykeeper.domain.holiday.dto;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
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

    public static HolidayDetailResponse toHolidayDetailResponse(
            Holiday holiday,
            List<String> counties,
            List<String> types
    ) {
        return new HolidayDetailResponse(
                holiday.getDate(),
                holiday.getLocalName(),
                holiday.getName(),
                holiday.getCountry().getName(),
                holiday.getCountry().getCode(),
                holiday.isGlobal(),
                counties,
                types
        );
    }

}
