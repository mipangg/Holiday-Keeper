package io.mipangg.holidaykeeper.domain.holidayCounty.service;

import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.service.CountyService;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayCountiesDto;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayCounty.dto.CountyElemDto;
import io.mipangg.holidaykeeper.domain.holidayCounty.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holidayCounty.repository.HolidayCountyRepository;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import io.mipangg.holidaykeeper.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
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
    public void saveHolidayCounties(
            List<HolidayCountiesDto> holidayCountiesDtos,
            Map<String, Holiday> holidayMap
    ) {

        Set<CountyElemDto> countyElemDtos = new HashSet<>();
        holidayCountiesDtos.forEach(dto -> {
                    if (holidayMap.get(dto.uniqueKey()) == null) {
                        throw new CustomLogicException(
                                ErrorCode.NOT_FOUND,
                                "uniqueKey와 일치하는 holiday를 찾을 수 없습니다."
                        );
                    }
                    countyElemDtos.add(
                            new CountyElemDto(
                                    holidayMap.get(dto.uniqueKey()).getCountry(),
                                    dto.counties())
                    );
                }
        );
        Map<String, County> counties = countyService.getOrCreateCounties(countyElemDtos);

        Set<HolidayCounty> holidayCounties = new HashSet<>();
        holidayCountiesDtos.forEach(dto -> {
            Holiday holiday = holidayMap.get(dto.uniqueKey());
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

    @Transactional
    public void deleteHolidayCounties(List<Long> holidayIds) {
        List<HolidayCounty> targetHolidayCounties =
                holidayCountyRepository.findByHolidayIdIn(holidayIds);

        if (targetHolidayCounties != null && !targetHolidayCounties.isEmpty()) {
            holidayCountyRepository.deleteAll(targetHolidayCounties);
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, List<String>> getHolidayCountyMapByHolidayIds(List<Long> holidayIds) {
        Map<Long, List<String>> holidayCountyMap = new HashMap<>();

        List<HolidayCounty> holidayCounties = holidayCountyRepository.findByHolidayIdIn(holidayIds);
        holidayCounties.forEach(holidayCounty -> {
            Long holidayId = holidayCounty.getHoliday().getId();
            String county = holidayCounty.getCounty().getCounty();
            holidayCountyMap
                    .computeIfAbsent(holidayId, k -> new ArrayList<>())
                    .add(county);
        });

        return holidayCountyMap;
    }
}
