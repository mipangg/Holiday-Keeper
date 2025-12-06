package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.service.CountyService;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayCountyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayCountyService {

    private final HolidayCountyRepository holidayCountyRepository;

    private final CountyService countyService;

    @Transactional
    public void saveIfNotExists(List<String> countyStrs, Country country, Holiday holiday) {
        if (countyStrs == null || countyStrs.isEmpty()) {
            return;
        }

        List<County> counties = countyService.saveIfNotExists(countyStrs, country);

        for (County county : counties) {
            holidayCountyRepository.findByCountyAndHoliday(county, holiday).orElseGet(() ->
                    holidayCountyRepository.save(
                            HolidayCounty.builder()
                                    .county(county)
                                    .holiday(holiday)
                                    .build()
                    )
            );
        }

    }

}
