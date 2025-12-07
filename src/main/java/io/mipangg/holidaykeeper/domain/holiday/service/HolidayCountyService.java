package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.service.CountyService;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayCountyRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (holiday.isGlobal() || countyStrs == null || countyStrs.isEmpty()) {
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

    @Transactional
    public void deleteHolidayCounties(List<Holiday> holidays) {
        List<Long> holidayIds = holidays.stream()
                .map(Holiday::getId)
                .toList();
        holidayCountyRepository.deleteByHolidays(holidayIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<HolidayCounty>> findByHolidays(List<Holiday> holidays) {
        Map<Long, List<HolidayCounty>> holidayCountyMap = new HashMap<>();
        for (HolidayCounty holidayCounty : holidayCountyRepository.findByHolidays(holidays)) {
            holidayCountyMap.computeIfAbsent(
                    holidayCounty.getHoliday().getId(),
                    k -> new ArrayList<>()
            ).add(holidayCounty);
        }

        return holidayCountyMap;
    }

    @Transactional
    public void upsertHolidayCounties(
            Holiday holiday,
            List<String> externalCountyNames,
            Country country
    ) {

        // 전역 공휴일이면 county 모두 삭제하고 리턴
        if (holiday.isGlobal()) {
            holidayCountyRepository.deleteByHoliday(holiday.getId());
            return;
        }

        if (externalCountyNames == null) {
            externalCountyNames = new ArrayList<>();
        }

        List<HolidayCounty> holidayCounties = holidayCountyRepository.findByHoliday(holiday);

        Map<String, HolidayCounty> holidayCountyMap = new HashMap<>();
        holidayCounties.forEach(holidayCounty -> {
            holidayCountyMap.put(holidayCounty.getCounty().getName(), holidayCounty);
        });

        List<HolidayCounty> toInsert = new ArrayList<>();
        List<HolidayCounty> toDelete = new ArrayList<>();

        for (String countyName : externalCountyNames) {
            HolidayCounty holidayCounty = holidayCountyMap.get(countyName);

            // 없으면 새로운 데이터 추가
            if (holidayCounty == null) {
                County county = countyService.findOrCreate(countyName, country);
                toInsert.add(
                        HolidayCounty.builder()
                                .holiday(holiday)
                                .county(county)
                                .build()
                );
            } else {
                // 있으면 기존 db 데이터 유지
                holidayCountyMap.remove(countyName);
            }
        }

        // 기존 db에 있지만 external에는 없음 -> 삭제 필요
        toDelete.addAll(holidayCountyMap.values());

        if (!toInsert.isEmpty()) {
            holidayCountyRepository.saveAll(toInsert);
        }
        if (!toDelete.isEmpty()) {
            holidayCountyRepository.deleteAll(toDelete);
        }
    }
}
