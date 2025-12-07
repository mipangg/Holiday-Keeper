package io.mipangg.holidaykeeper.domain.holiday.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

public record HolidaySearchRequest(

        @Min(0)
        @Max(10000)
        Integer page,

        @Min(0)
        @Max(100)
        Integer size,

        @Nullable
        LocalDate from,

        @Nullable
        LocalDate to,

        @Nullable
        String holidayType

) {
    public HolidaySearchRequest {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 20;
        }
    }

}
