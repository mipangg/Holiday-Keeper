package io.mipangg.holidaykeeper.domain.holiday.dto;

import java.time.LocalDate;
import java.util.List;

public record HolidayListReadResponse(
        Long id,
        LocalDate date,
        String localName,
        String name,
        String country,
        List<String> counties,
        List<String> types
) {
}
