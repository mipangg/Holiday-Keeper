package io.mipangg.holidaykeeper.domain.holidayCounty.service;

import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.service.CountyService;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayCountiesDto;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayCounty.dto.CountyElemDto;
import io.mipangg.holidaykeeper.domain.holidayCounty.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holidayCounty.repository.HolidayCountyRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayCountyService {

    private final HolidayCountyRepository holidayCountyRepository;

    private final CountyService countyService;

    @Transactional
    public void saveHolidayCounties(List<HolidayCountiesDto> holidayCountiesDtos) {
        Set<CountyElemDto> countyElemDtos = new HashSet<>();
        holidayCountiesDtos.forEach(dto ->
                countyElemDtos.add(new CountyElemDto(dto.holiday().getCountry(), dto.counties()))
        );
        Map<String, County> counties = countyService.getOrCreateCounties(countyElemDtos);

        Set<HolidayCounty> holidayCounties = new HashSet<>();
        holidayCountiesDtos.forEach(dto -> {
            Holiday holiday = dto.holiday();
            dto.counties().forEach(countyCode ->
                    holidayCounties.add(
                            HolidayCounty.builder()
                                    .county(counties.get(countyCode))
                                    .holiday(holiday)
                                    .build()
                    )
            );
        });

        holidayCountyRepository.saveAll(holidayCounties);
    }

}
