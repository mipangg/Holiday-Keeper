package io.mipangg.holidaykeeper.domain.holidayCounty.dto;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import java.util.List;

public record CountyElemDto(
        Country country,
        List<String> countyNames
) {

}
