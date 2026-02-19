package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.util.TestUtil.genCountries;
import static io.mipangg.holidaykeeper.util.TestUtil.genHolidayKorea;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.common.PageResponse;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayListReadResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayReadRequest;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.properties.HolidayProperties;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.domain.holidayCounty.service.HolidayCountyService;
import io.mipangg.holidaykeeper.domain.holidayType.service.HolidayTypeService;
import io.mipangg.holidaykeeper.external.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTests {

    @InjectMocks
    private HolidayService holidayService;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private ExternalApiClient externalApiClient;

    @Mock
    private HolidayProperties holidayProperties;

    @Mock
    private HolidayCountyService holidayCountyService;

    @Mock
    private HolidayTypeService holidayTypeService;

    @Test
    @DisplayName("holiday 저장 테스트")
    void saveHolidaysTest() {

        int fetchYear = 1;

        Map<String, Country> countries = new HashMap<>();
        genCountries().forEach(c -> {
            countries.put(c.getCountryCode(), c);
        });

        List<ExternalHolidayResponse> resp = List.of(
                new ExternalHolidayResponse(
                        "2026-01-01",
                        "새해",
                        "New Year's Day",
                        "KR",
                        false,
                        true,
                        null,
                        null,
                        List.of("Public")
                )
        );

        when(externalApiClient.getExternalHolidays(anyInt(), anyString())).thenReturn(resp);
        when(holidayRepository.existsBy()).thenReturn(Boolean.FALSE);
        when(holidayProperties.getFetchYears()).thenReturn(fetchYear);

        holidayService.saveHolidays(countries);

        verify(externalApiClient, times(fetchYear * countries.size())).getExternalHolidays(anyInt(),
                anyString());
        verify(holidayRepository).existsBy();
        verify(holidayRepository).saveAll(anySet());

    }

    @Test
    @DisplayName("holiday에 저장된 데이터가 있을 때 create를 시도한 경우 409 발생 테스트")
    void saveHolidays409FailTest() {

        Map<String, Country> countries = new HashMap<>();
        genCountries().forEach(c -> {
            countries.put(c.getCountryCode(), c);
        });

        when(holidayRepository.existsBy()).thenReturn(Boolean.TRUE);

        assertThatThrownBy(
                () -> {
                    holidayService.saveHolidays(countries);
                }
        ).isInstanceOf(CustomLogicException.class)
                .hasMessage("이미 존재하는 데이터입니다.");

    }

    @Test
    @DisplayName("holiday 삭제 테스트")
    void deleteHolidaysTest() {

        int year = 2026;
        String countryCode = "KR";

        List<Holiday> targetHolidays = List.of(
                genHolidayKorea()
        );

        when(holidayRepository.findByYearAndCountryCode(year, countryCode))
                .thenReturn(targetHolidays);

        holidayService.deleteHolidays(year, countryCode);

        verify(holidayRepository).findByYearAndCountryCode(year, countryCode);
        verify(holidayRepository).deleteAll(targetHolidays);

    }

    @Test
    @DisplayName("삭제하려는 연도, 국가코드에 해당하는 공휴일 목록이 없을 때 404 발생 테스트")
    void deleteHolidays404FailTest() {

        when(holidayRepository.findByYearAndCountryCode(anyInt(), anyString()))
                .thenReturn(List.of());

        assertThatThrownBy(
                () -> {
                    holidayService.deleteHolidays(anyInt(), anyString());
                }
        ).isInstanceOf(CustomLogicException.class);

    }
    
    @Test
    @DisplayName("특정 연도·국가의 공휴일 목록 조회 테스트")
    void searchHolidaysTest() {
    
        int year = 2026;
        String countryCode = "KR";
        HolidayReadRequest req = new HolidayReadRequest(0, 20, null, null, null);

        Holiday holiday = genHolidayKorea();

        Page<Holiday> page = new PageImpl<>(List.of(holiday), PageRequest.of(0, 20), 1);

        when(holidayRepository.searchHoliday(
                anyInt(), anyString(), any(), any(), any(), any())
        ).thenReturn(page);
        when(holidayCountyService.getHolidayCountyMapByHolidayIds(anyList()))
                .thenReturn(Collections.emptyMap());
        when(holidayTypeService.getHolidayTypeMapByHolidayIds(anyList()))
                .thenReturn(Collections.emptyMap());

        PageResponse<HolidayListReadResponse> result =
                holidayService.searchHolidays(year, countryCode, req);

        assertThat(result.getContent().getFirst().localName()).isEqualTo("새해");
        assertThat(result.getContent().getFirst().name()).isEqualTo("New Year's Day");
    }

}