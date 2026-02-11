package io.mipangg.holidaykeeper.domain.holidayType.service;

import static io.mipangg.holidaykeeper.util.TestUtil.getHolidayBrazil;
import static io.mipangg.holidaykeeper.util.TestUtil.getHolidayCanada;
import static io.mipangg.holidaykeeper.util.TestUtil.getHolidayKorea;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayTypesDto;
import io.mipangg.holidaykeeper.domain.holidayType.repository.HolidayTypeRepository;
import io.mipangg.holidaykeeper.domain.type.entity.Type;
import io.mipangg.holidaykeeper.domain.type.service.TypeService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidayTypeServiceTests {

    @InjectMocks
    private HolidayTypeService holidayTypeService;

    @Mock
    private HolidayTypeRepository holidayTypeRepository;

    @Mock
    private TypeService typeService;

    @Test
    @DisplayName("holiday 저장 시 holidayType도 저장하는 기능 테스트")
    void saveHolidayTypesTest() {

        List<HolidayTypesDto> holidayTypesDtos = List.of(
                new HolidayTypesDto(
                        getHolidayBrazil(),
                        List.of("Public", "Bank")
                ),
                new HolidayTypesDto(
                        getHolidayKorea(),
                        List.of("Public")
                )
        );

        Map<String, Type> typeMap = Map.of(
                "Public", Type.builder().type("Public").build(),
                "Bank", Type.builder().type("Bank").build()
        );

        when(typeService.getOrCreateTypes(Set.of("Public", "Bank"))).thenReturn(typeMap);

        holidayTypeService.saveHolidayTypes(holidayTypesDtos);

        verify(holidayTypeRepository).saveAll(anyList());
    }

}