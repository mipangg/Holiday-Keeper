package io.mipangg.holidaykeeper.domain.holidayType.service;

import static io.mipangg.holidaykeeper.util.TestUtil.genHolidayBrazil;
import static io.mipangg.holidaykeeper.util.TestUtil.genHolidayCanada;
import static io.mipangg.holidaykeeper.util.TestUtil.genHolidayKorea;
import static io.mipangg.holidaykeeper.util.TestUtil.genTypeBank;
import static io.mipangg.holidaykeeper.util.TestUtil.genTypePublic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayTypesDto;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayType.entity.HolidayType;
import io.mipangg.holidaykeeper.domain.holidayType.repository.HolidayTypeRepository;
import io.mipangg.holidaykeeper.domain.type.entity.Type;
import io.mipangg.holidaykeeper.domain.type.service.TypeService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
                        genHolidayBrazil(),
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

    @Test
    @DisplayName("holidayType 삭제 테스트")
    void deleteHolidayTypesTest() {

        List<Holiday> holidays = List.of(genHolidayBrazil());
        List<Long> holidayIds = holidays.stream()
                .map(Holiday::getId)
                .collect(Collectors.toList());
        List<HolidayType> targetHolidayTypes = List.of(
                HolidayType.builder()
                        .type(Type.builder().type("Public").build())
                        .holiday(holidays.getFirst())
                        .build(),
                HolidayType.builder()
                        .type(Type.builder().type("Bank").build())
                        .holiday(holidays.getFirst())
                        .build()
        );

        when(holidayTypeRepository.findByHolidayIdIn(holidayIds)).thenReturn(targetHolidayTypes);

        holidayTypeService.deleteHolidayTypes(holidayIds);

        verify(holidayTypeRepository).findByHolidayIdIn(anyList());
        verify(holidayTypeRepository).deleteAll(targetHolidayTypes);

    }

    @Test
    @DisplayName("holidayId 리스트로 holidayType을 조회하여 Map으로 반환하는 기능 테스트")
    void getHolidayTypeMapByHolidayIdsTest() {

        List<Long> holidayIds = List.of(1L, 2L, 3L);
        List<HolidayType> holidayTypes = List.of(
                HolidayType.builder().holiday(genHolidayBrazil()).type(genTypePublic()).build(),
                HolidayType.builder().holiday(genHolidayBrazil()).type(genTypeBank()).build(),
                HolidayType.builder().holiday(genHolidayCanada()).type(genTypePublic()).build(),
                HolidayType.builder().holiday(genHolidayKorea()).type(genTypePublic()).build()
        );

        when(holidayTypeRepository.findByHolidayIdIn(holidayIds)).thenReturn(holidayTypes);

        Map<Long, List<String>> result = holidayTypeService.getHolidayTypeMapByHolidayIds(holidayIds);

        assertThat(result.get(1L)).hasSize(2);
        assertThat(result.get(2L)).hasSize(1);
        assertThat(result.get(3L)).hasSize(1);
        assertThat(result.get(1L).getFirst()).isEqualTo("Public");
        assertThat(result.get(1L).getLast()).isEqualTo("Bank");
        assertThat(result.get(2L).getFirst()).isEqualTo("Public");
        assertThat(result.get(3L).getFirst()).isEqualTo("Public");
    }

}