package io.mipangg.holidaykeeper.domain.holidayType.service;

import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayTypesDto;
import io.mipangg.holidaykeeper.domain.holidayType.entity.HolidayType;
import io.mipangg.holidaykeeper.domain.holidayType.repository.HolidayTypeRepository;
import io.mipangg.holidaykeeper.domain.type.entity.Type;
import io.mipangg.holidaykeeper.domain.type.service.TypeService;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import io.mipangg.holidaykeeper.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayTypeService {

    private final HolidayTypeRepository holidayTypeRepository;

    private final TypeService typeService;

    @Transactional
    public void saveHolidayTypes(List<HolidayTypesDto> holidayTypesDtos) {

        Set<String> typeNames = new HashSet<>();
        holidayTypesDtos.forEach(dto -> typeNames.addAll(dto.types()));
        Map<String, Type> typeMap = typeService.getOrCreateTypes(typeNames);

        List<HolidayType> holidayTypes = new ArrayList<>();
        holidayTypesDtos.forEach(dto ->
                dto.types().forEach(type ->
                        holidayTypes.add(
                                HolidayType.builder()
                                        .holiday(dto.holiday())
                                        .type(typeMap.get(type))
                                        .build()
                        )
                )
        );
        try {
            holidayTypeRepository.saveAll(holidayTypes);
        } catch (DataIntegrityViolationException e) {
            throw new CustomLogicException(
                    ErrorCode.CONFLICT,
                    "HolidayType 테이블에 데이터가 이미 존재합니다."
            );
        }

    }

}
