package io.mipangg.holidaykeeper.domain.holiday.dto;

import java.util.Collection;

public record HolidayTypesDto(
        String uniqueKey,
        Collection<String> types
) {

}
