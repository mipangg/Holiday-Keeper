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

    @Transactional
    public void upsertHolidayTypes(Holiday holiday, List<String> externalTypes) {
        if (holiday == null) {
            return;
        }

        // type이 비었어도 아래 로직 수행 필요
        if (externalTypes == null || externalTypes.isEmpty()) {
            externalTypes = new ArrayList<>();
        }

        List<HolidayType> holidayTypes = holidayTypeRepository.findByHoliday(holiday);

        Map<String, HolidayType> holidayTypeMap = new HashMap<>();
        holidayTypes.forEach(holidayType -> {
            holidayTypeMap.put(holidayType.getType(), holidayType);
        });

        List<HolidayType> toInsert = new ArrayList<>();
        List<HolidayType> toDelete = new ArrayList<>();

        for (String externalType : externalTypes) {
            HolidayType holidayType = holidayTypeMap.get(externalType);

            // 없으면 새로운 데이터 추가
            if (holidayType == null) {
                toInsert.add(
                        HolidayType.builder()
                        .holiday(holiday)
                        .type(externalType)
                        .build()
                );

            } else {
                // 있으면 기존 db 데이터 유지
                holidayTypeMap.remove(externalType);
            }
        }

        // 기존 db에 있지만 external에는 없음 -> 삭제 필요
        toDelete.addAll(holidayTypeMap.values());

        if (!toInsert.isEmpty()) {
            holidayTypeRepository.saveAll(toInsert);
        }
        if (!toDelete.isEmpty()) {
            holidayTypeRepository.deleteAll(toDelete);
        }
    }

}
