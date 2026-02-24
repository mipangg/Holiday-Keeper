package io.mipangg.holidaykeeper.domain.holidayCounty.service;

import static io.mipangg.holidaykeeper.util.TestUtil.genCountryCanada;
import static io.mipangg.holidaykeeper.util.TestUtil.genCountyAb;
import static io.mipangg.holidaykeeper.util.TestUtil.genCountyPe;
import static io.mipangg.holidaykeeper.util.TestUtil.genHolidayCanada;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.service.CountyService;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayCountiesDto;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayCounty.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holidayCounty.repository.HolidayCountyRepository;
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
class HolidayCountyServiceTests {

    @InjectMocks
    private HolidayCountyService holidayCountyService;

    @Mock
    private HolidayCountyRepository holidayCountyRepository;

    @Mock
    private CountyService countyService;

    @Mock
    private CountryRepository countryRepository;

    @Test
    @DisplayName("holidayCounty 저장 기능 테스트")
    void saveHolidayCountiesTest() {
        Holiday holidayCanada = genHolidayCanada();
        List<HolidayCountiesDto> holidayCountiesDtos = List.of(
                new HolidayCountiesDto(
                        "2026-04-06|Easter Monday|CA",
                        List.of(
                                "CA-AB",
                                "CA-PE"
                        )
                )
        );
        Map<String, County> counties = Map.of(
                "CA-AB", genCountyAb(),
                "CA-PE", genCountyPe()
        );

        when(countyService.getOrCreateCounties(anySet())).thenReturn(counties);

        holidayCountyService.saveHolidayCounties(
                holidayCountiesDtos,
                Map.of("2026-04-06|Easter Monday|CA", holidayCanada)
        );

        verify(holidayCountyRepository).saveAll(anySet());
    }

    @Test
    @DisplayName("holidayCounty 삭제 테스트")
    void deleteHolidayCountiesTest() {
        List<Holiday> holidays = List.of(genHolidayCanada());
        List<Long> holidayIds = holidays.stream()
                .map(Holiday::getId)
                .collect(Collectors.toList());
        List<HolidayCounty> targetHolidayCounties = List.of(
                HolidayCounty.builder()
                        .county(genCountyAb())
                        .build(),
                HolidayCounty.builder()
                        .county(genCountyPe())
                        .build()
        );

        when(holidayCountyRepository.findByHolidayIdIn(holidayIds)).thenReturn(targetHolidayCounties);

        holidayCountyService.deleteHolidayCounties(holidayIds);

        verify(holidayCountyRepository).findByHolidayIdIn(holidayIds);
        verify(holidayCountyRepository).deleteAll(targetHolidayCounties);

    }

    @Test
    @DisplayName("holidayId 목록으로 holidayCounty를 조회하여 map으로 반환하는 기능 테스트")
    void getHolidayCountyMapByHolidayIdsTest() {

        List<Long> holidayIds = List.of(1L, 2L);
        List<HolidayCounty> holidayCounties = List.of(
                HolidayCounty.builder().holiday(genHolidayCanada()).county(genCountyAb()).build(),
                HolidayCounty.builder().holiday(genHolidayCanada()).county(genCountyPe()).build()
        );

        when(holidayCountyRepository.findByHolidayIdIn(holidayIds)).thenReturn(holidayCounties);

        Map<Long, List<String>> result = holidayCountyService
                .getHolidayCountyMapByHolidayIds(holidayIds);

        assertThat(result.get(2L)).hasSize(2);
        assertThat(result.get(2L).getFirst()).isEqualTo("CA-AB");
        assertThat(result.get(2L).getLast()).isEqualTo("CA-PE");
    }

    @Test
    @DisplayName("upsert된 holiday와 연관된 holidayCounty 업데이트 기능 테스트")
    void upsertHolidayCountiesTest() {

        Holiday holiday = genHolidayCanada();
        String uniqueKey = "2026-04-06|Easter Monday|CA";
        List<HolidayCountiesDto> requestHolidayCountiesDtos = List.of(
                new HolidayCountiesDto(
                        uniqueKey,
                        List.of(
                                "CA-AB",
                                "CA-PE"
                        )
                )
        );
        Map<String, Holiday> requestHolidayMap = Map.of(
                uniqueKey, holiday
        );
        List<HolidayCounty> existingHolidayCounties = List.of(
                HolidayCounty.builder()
                .holiday(holiday)
                .county(genCountyAb())
                .build()
        );
        List<Country> requestCountries = List.of(
                genCountryCanada()
        );
        Map<String, County> insertedCountiesInHolidayCounty = Map.of(
                "CA-PE",
                genCountyPe()
        );

        when(holidayCountyRepository.findByHolidayIdIn(anyList()))
                .thenReturn(existingHolidayCounties);
        when(countryRepository.findByCountryCodeIn(anySet())).thenReturn(requestCountries);
        when(countyService.getOrCreateCounties(anySet()))
                .thenReturn(insertedCountiesInHolidayCounty);

        holidayCountyService.upsertHolidayCounties(
                requestHolidayCountiesDtos,
                requestHolidayMap
        );

        verify(holidayCountyRepository).saveAll(anySet());

    }

}