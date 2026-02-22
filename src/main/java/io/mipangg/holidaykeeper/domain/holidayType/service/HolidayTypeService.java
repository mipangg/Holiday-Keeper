package io.mipangg.holidaykeeper.domain.holidayType.service;

import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayTypesDto;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayType.entity.HolidayType;
import io.mipangg.holidaykeeper.domain.holidayType.repository.HolidayTypeRepository;
import io.mipangg.holidaykeeper.domain.type.entity.Type;
import io.mipangg.holidaykeeper.domain.type.service.TypeService;
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
public class HolidayTypeService {

    private final HolidayTypeRepository holidayTypeRepository;

    private final TypeService typeService;

    @Transactional
    public void saveHolidayTypes(
            List<HolidayTypesDto> holidayTypesDtos,
            Map<String, Holiday> holidayMap
    ) {
        Set<String> typeNames = new HashSet<>();
        holidayTypesDtos.forEach(dto -> typeNames.addAll(dto.types()));

        List<HolidayType> holidayTypes = new ArrayList<>();
        Map<String, Type> types = typeService.getOrCreateTypes(typeNames);
        holidayTypesDtos.forEach(dto -> {
            Holiday holiday = holidayMap.get(dto.uniqueKey());
            if (holiday == null) {
                throw new CustomLogicException(
                        ErrorCode.NOT_FOUND,
                        "uniqueKey와 일치하는 holiday를 찾을 수 없습니다."
                );
            }
            dto.types().forEach(type ->
                    holidayTypes.add(
                            HolidayType.builder()
                                    .holiday(holiday)
                                    .type(types.get(type))
                                    .build()
                    )
            );
        });

        holidayTypeRepository.saveAll(holidayTypes);
    }

    @Transactional
    public void deleteHolidayTypes(List<Long> holidayIds) {
        List<HolidayType> targetHolidayTypes = holidayTypeRepository.findByHolidayIdIn(holidayIds);

        if (!targetHolidayTypes.isEmpty()) {
            holidayTypeRepository.deleteAll(targetHolidayTypes);
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, List<String>> getHolidayTypeMapByHolidayIds(List<Long> holidayIds) {
        Map<Long, List<String>> holidayTypeMap = new HashMap<>();

        List<HolidayType> holidayTypes = holidayTypeRepository.findByHolidayIdIn(holidayIds);
        holidayTypes.forEach(holidayType -> {
            Long holidayId = holidayType.getHoliday().getId();
            String type = holidayType.getType().getType();
            holidayTypeMap
                    .computeIfAbsent(holidayId, k -> new ArrayList<>())
                    .add(type);
        });

        return holidayTypeMap;
    }
}
