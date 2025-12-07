package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayType;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayTypeRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayTypeService {

    private final HolidayTypeRepository holidayTypeRepository;

    @Transactional
    public void saveIfNotExists(List<String> holidayTypes, Holiday holiday) {
        for (String holidayType : holidayTypes) {
            holidayTypeRepository.findByTypeAndHoliday_Id(holidayType, holiday.getId())
                    .orElseGet(() ->
                            holidayTypeRepository.save(
                                    HolidayType.builder()
                                            .type(holidayType)
                                            .holiday(holiday)
                                            .build()
                            )
                    );

        }
    }

    @Transactional
    public void deleteHolidayTypes(List<Holiday> holidays) {
        List<Long> holidayIds = holidays.stream()
                .map(Holiday::getId)
                .toList();
        holidayTypeRepository.deleteByHolidays(holidayIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<HolidayType>> findHolidayTypes(List<Holiday> holidays) {
        Map<Long, List<HolidayType>> holidayTypeMap = new HashMap<>();

        for (HolidayType holidayType : holidayTypeRepository.findByHolidays(holidays)) {
            holidayTypeMap.computeIfAbsent(
                            holidayType.getHoliday().getId(),
                            k -> new ArrayList<>()
                    )
                    .add(holidayType);
        }

        return holidayTypeMap;
    }

}
