package io.mipangg.holidaykeeper.domain.holidayCounty.service;

import static io.mipangg.holidaykeeper.util.TestUtil.genHolidayCanada;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.service.CountyService;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayCountiesDto;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayCounty.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holidayCounty.repository.HolidayCountyRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidayCountyServiceTests {

    @InjectMocks
    private HolidayCountyService holidayCountyService;

    @Mock
    private HolidayCountyRepository holidayCountyRepository;

    @Mock
    private CountyService countyService;

    @Test
    @DisplayName("holidayCounty 저장 기능 테스트")
    void saveHolidayCountiesTest() {
        Holiday holidayCanada = genHolidayCanada();
        Country canada = holidayCanada.getCountry();
        List<HolidayCountiesDto> holidayCountiesDtos = List.of(
                new HolidayCountiesDto(
                        holidayCanada,
                        List.of(
                                "CA-AB",
                                "CA-PE"
                        )
                )
        );
        Map<String, County> counties = Map.of(
                "CA-AB", County.builder().county("CA-AB").country(canada).build(),
                "CA-PE", County.builder().county("CA-PE").country(canada).build()
        );

        when(countyService.getOrCreateCounties(anySet())).thenReturn(counties);

        holidayCountyService.saveHolidayCounties(holidayCountiesDtos);

        verify(holidayCountyRepository).saveAll(anySet());
    }

    @Test
    @DisplayName("holidayCounty 삭제 테스트")
    void deleteHolidayCountiesTest() {
        List<Holiday> holidays = List.of(genHolidayCanada());
        Country canada = holidays.getFirst().getCountry();
        List<HolidayCounty> targetHolidayCounties = List.of(
                HolidayCounty.builder()
                        .county(
                                County.builder()
                                        .county("CA-AB")
                                        .country(canada)
                                        .build()
                        )
                        .build(),
                HolidayCounty.builder()
                        .county(
                                County.builder()
                                        .county("CA-PE")
                                        .country(canada)
                                        .build()
                        )
                        .build()
        );

        when(holidayCountyRepository.findByHolidayIn(holidays)).thenReturn(targetHolidayCounties);

        holidayCountyService.deleteHolidayCounties(holidays);

        verify(holidayCountyRepository).findByHolidayIn(holidays);
        verify(holidayCountyRepository).deleteAll(targetHolidayCounties);

    }

}