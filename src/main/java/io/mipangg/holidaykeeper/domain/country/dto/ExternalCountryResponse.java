package io.mipangg.holidaykeeper.domain.country.dto;

import jakarta.validation.constraints.NotBlank;

public record ExternalCountryResponse(
        @NotBlank
        String countryCode,
        @NotBlank
        String name
) {


}
