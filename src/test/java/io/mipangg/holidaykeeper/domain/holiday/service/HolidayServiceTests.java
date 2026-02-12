package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.util.TestUtil.getCountries;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.properties.HolidayProperties;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.domain.holidayCounty.service.HolidayCountyService;
import io.mipangg.holidaykeeper.domain.holidayType.service.HolidayTypeService;
import io.mipangg.holidaykeeper.external.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTests {

    @InjectMocks
    private HolidayService holidayService;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private ExternalApiClient externalApiClient;

    @Mock
    private HolidayCountyService holidayCountyService;

    @Mock
    private HolidayTypeService holidayTypeService;

    @Mock
    private HolidayProperties holidayProperties;

    @Test
    @DisplayName("holiday 저장 테스트")
    void saveHolidaysTest() {

        int fetchYear = 1;

        Map<String, Country> countries = new HashMap<>();
        getCountries().forEach(c -> {
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
        getCountries().forEach(c -> {
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

}