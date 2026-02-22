package io.mipangg.holidaykeeper.domain.holiday.dto;

import java.util.List;

public record HolidayTypesDto(
        String uniqueKey,
        List<String> types
) {

}
