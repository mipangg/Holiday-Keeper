package io.mipangg.holidaykeeper.domain.holidayType.service;

import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayTypesDto;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayType.entity.HolidayType;
import io.mipangg.holidaykeeper.domain.holidayType.repository.HolidayTypeRepository;
import io.mipangg.holidaykeeper.domain.type.entity.Type;
import io.mipangg.holidaykeeper.domain.type.service.TypeService;
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
public class HolidayTypeService {

    private final HolidayTypeRepository holidayTypeRepository;

    private final TypeService typeService;

    @Transactional
    public void saveHolidayTypes(List<HolidayTypesDto> holidayTypesDtos) {
        Set<String> typeNames = new HashSet<>();
        holidayTypesDtos.forEach(dto -> typeNames.addAll(dto.types()));

        List<HolidayType> holidayTypes = new ArrayList<>();
        Map<String, Type> types = typeService.getOrCreateTypes(typeNames);
        holidayTypesDtos.forEach(dto -> {
            Holiday holiday = dto.holiday();
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
}
