package io.mipangg.holidaykeeper.domain.holiday.dto;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import java.util.List;
import java.util.Set;

public record HolidayCreationResultDto(
        Set<Holiday> holidays,
        List<HolidayCountiesDto> holidayCountiesDtos,
        List<HolidayTypesDto> holidayTypesDtos
) {

}
