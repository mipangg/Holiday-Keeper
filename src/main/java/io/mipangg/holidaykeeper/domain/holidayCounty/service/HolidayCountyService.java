package io.mipangg.holidaykeeper.domain.holidayCounty.service;

import io.mipangg.holidaykeeper.domain.county.service.CountyService;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayCountiesDto;
import io.mipangg.holidaykeeper.domain.holidayCounty.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holidayCounty.repository.HolidayCountyRepository;
import java.util.ArrayList;
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
    public void saveHolidayCounties(List<HolidayCountiesDto> holidayCountiesDtos) {
        List<HolidayCounty> holidayCounties = new ArrayList<>();

        

        holidayCountyRepository.saveAll(holidayCounties);
    }

}
