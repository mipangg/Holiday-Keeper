package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.util.TestUtils.getCountryMap;
import static io.mipangg.holidaykeeper.util.TestUtils.getLastFiveYears;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.common.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import java.time.LocalDate;
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
    private HolidaySupportService holidaySupportService;

    @Test
    @DisplayName("country 정보와 최근 5년의 연도 정보로 중복 검사 후 holiday를 저장할 수 있다")
    void saveHolidaysInLastFiveYears_success() {

        Map<String, Country> countryMap = getCountryMap();
        List<Integer> lastFiveYears = getLastFiveYears();
        List<ExternalHolidayResponse> externalHolidayResps = List.of(
                new ExternalHolidayResponse(
                        LocalDate.of(2025, 01, 01),
                        "Confraternização Universal",
                        "New Year's Day",
                        "BR",
                        false,
                        true,
                        null,
                        null,
                        List.of("Public")
                ),
                new ExternalHolidayResponse(
                        LocalDate.of(2025, 01, 01),
                        "New Year's Day",
                        "New Year's Day",
                        "CA",
                        false,
                        true,
                        null,
                        null,
                        List.of("Public")
                ),
                new ExternalHolidayResponse(
                        LocalDate.of(2025, 01, 28),
                        "설날",
                        "Lunar New Year",
                        "KR",
                        false,
                        true,
                        null,
                        null,
                        List.of("Public")
                )
        );

        when(holidaySupportService.saveAndGetCountries()).thenReturn(countryMap);
        when(holidaySupportService.getLastFiveYears()).thenReturn(lastFiveYears);
        when(holidaySupportService.getExternalHolidayResps(lastFiveYears, countryMap.keySet()))
                .thenReturn(externalHolidayResps);

        when(holidayRepository.existsByDateAndNameAndCountry(
                eq(LocalDate.of(2025, 01, 01)), eq("New Year's Day"), any(Country.class))
        ).thenReturn(false);
        when(holidayRepository.existsByDateAndNameAndCountry(
                eq(LocalDate.of(2025, 01, 28)), eq("Lunar New Year"), any(Country.class))
        ).thenReturn(true);

        holidayService.saveHolidaysInLastFiveYears();

        verify(holidayRepository, times(2)).existsByDateAndNameAndCountry(
                eq(LocalDate.of(2025, 01, 01)), eq("New Year's Day"), any(Country.class)
        );
        verify(holidayRepository, times(1)).existsByDateAndNameAndCountry(
                eq(LocalDate.of(2025, 01, 28)), eq("Lunar New Year"), any(Country.class)
        );

        verify(holidayRepository, times(2)).save(any(Holiday.class));
    }

}