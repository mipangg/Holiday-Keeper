package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.util.TestUtils.getCountryCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getHolidayCanada;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.domain.holidayType.service.HolidayTypeService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private ExternalHolidayClient externalHolidayClient;

    @Mock
    private CountryService countryService;

    @Mock
    private HolidayTypeService holidayTypeService;

    @Mock
    private HolidayCountyService holidayCountyService;

    @Test
    @DisplayName("최근 5년의 공휴일을 외부 api에 수집하여 저장할 수 있다")
    void syncHolidays_success_test() {

        Map<String, Country> countryMap = Map.of("Canada", getCountryCanada());
        List<ExternalHolidayResponse> externalHolidays = List.of(
                new ExternalHolidayResponse(
                        "2025-02-17",
                        "Family Day",
                        "Family Day",
                        "CA",
                        false,
                        false,
                        List.of(
                                "CA-AB",
                                "CA-BC",
                                "CA-NB",
                                "CA-ON",
                                "CA-SK"
                        ),
                        null,
                        List.of("Public")
                ));

        Holiday holiday = getHolidayCanada();

        when(countryService.findAll()).thenReturn(countryMap);
        when(externalHolidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(externalHolidays);
        when(holidayRepository.findByDateAndCountry(any(LocalDate.class), any(Country.class)))
                .thenReturn(Optional.empty());
        when(holidayRepository.save(any(Holiday.class))).thenReturn(holiday);

        holidayService.syncHolidays();

        verify(holidayCountyService, times(6))
                .saveIfNotExists(anyList(), any(Country.class), eq(holiday));
        verify(holidayTypeService, times(6))
                .saveIfNotExists(anyList(), eq(holiday));
        verify(holidayRepository, times(6))
                .findByDateAndCountry(any(LocalDate.class), any(Country.class));
        verify(holidayRepository, times(6)).save(any(Holiday.class));

    }

}