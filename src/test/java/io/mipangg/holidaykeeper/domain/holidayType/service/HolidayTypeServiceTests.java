package io.mipangg.holidaykeeper.domain.holidayType.service;

import static io.mipangg.holidaykeeper.util.TestUtil.getHolidayBrazil;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
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
    @DisplayName("holidayType 저장 테스트")
    void saveHolidayTypesTest() {

        List<HolidayTypesDto> holidayTypesDtos = List.of(
                new HolidayTypesDto(
                        getHolidayBrazil(),
                        List.of("Public", "Bank")
                )
        );
        Map<String, Type> types = Map.of(
                "Public", Type.builder().type("Public").build(),
                "Bank", Type.builder().type("Bank").build()
        );

        when(typeService.getOrCreateTypes(anySet())).thenReturn(types);

        holidayTypeService.saveHolidayTypes(holidayTypesDtos);

        verify(holidayTypeRepository).saveAll(anyList());

    }

}