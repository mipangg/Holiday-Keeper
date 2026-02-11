package io.mipangg.holidaykeeper.domain.holiday.dto;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.util.List;

public record HolidayTypesDto(
        Holiday holiday,
        List<String> types
) {

}
