package io.mipangg.holidaykeeper.domain.holiday.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ExternalHolidayResponse(
        @NotBlank
        String date,
        @NotBlank
        String localName,
        @NotBlank
        String name,
        @NotBlank
        String countryCode,
        boolean fixed,
        boolean global,
        List<String> counties,
        Integer launchYear,
        @NotNull
        List<String> types
) {

}
