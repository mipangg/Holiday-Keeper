package io.mipangg.holidaykeeper.external.dto;

import java.util.List;

public record ExternalHolidayResponse(
        String date,
        String localName,
        String name,
        String countryCode,
        boolean fixed,
        boolean global,
        List<String> counties,
        Integer launchYear,
        List<String> types
) {

}
