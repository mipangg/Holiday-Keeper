package io.mipangg.holidaykeeper.domain.holiday.dto;

import java.util.List;

public record HolidayCountiesDto(
        String uniqueKey,
        List<String> counties
) {

}
