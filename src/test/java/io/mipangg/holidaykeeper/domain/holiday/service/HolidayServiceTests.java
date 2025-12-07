package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.util.TestUtils.getCountryCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getHolidayCanada;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
        when(holidayRepository.findByDateAndCountryAndNameAndIsGlobal(
                any(LocalDate.class), any(Country.class), anyString(), anyBoolean()
        ))
                .thenReturn(Optional.empty());
        when(holidayRepository.save(any(Holiday.class))).thenReturn(holiday);

        holidayService.syncHolidays();

        verify(holidayCountyService, times(6))
                .saveIfNotExists(anyList(), any(Country.class), eq(holiday));
        verify(holidayTypeService, times(6))
                .saveIfNotExists(anyList(), eq(holiday));
        verify(holidayRepository, times(6))
                .findByDateAndCountryAndNameAndIsGlobal(
                        any(LocalDate.class), any(Country.class), anyString(), anyBoolean()
                );
        verify(holidayRepository, times(6)).save(any(Holiday.class));

    }

    @Test
    @DisplayName("year과 countryCode를 인자로 받아 특정 holiday 리스트를 삭제할 수 있다")
    void deleteHolidays_success_test() {

        int year = 2025;
        String countryCode = "CA";
        List<Holiday> targetHolidays = List.of(getHolidayCanada());

        when(holidayRepository.findByYearAndCountryCode(year, countryCode)).thenReturn(targetHolidays);

        holidayService.deleteHolidays(year, countryCode);

        verify(holidayRepository, times(1)).deleteAll(targetHolidays);

    }

    @Test
    @DisplayName("year과 countryCode에 속하는 holiday 리스트가 없는데 삭제를 시도하면 예외가 발생한다")
    void deleteHolidays_fail_test() {

        int year = 2019;
        String countryCode = "CA";

        when(holidayRepository.findByYearAndCountryCode(year, countryCode)).thenReturn(List.of());

        assertThatThrownBy(
                () -> {
                    holidayService.deleteHolidays(year, countryCode);
                }
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        String.format("%d년 %s에 해당하는 holiday를 찾을 수 없습니다.", year, countryCode)
                );
    }

    @Test
    @DisplayName("year과 country를 인자로 받아 기존 데이터가 존재하면 삭제하고 다시 저장한다")
    void updateHolidays_success_test() {

        int year = 2025;
        String countryCode = "CA";
        Country country = getCountryCanada();
        Holiday holiday = getHolidayCanada();
        List<Holiday> holidays = List.of(holiday);
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

        when(countryService.getByCode(countryCode)).thenReturn(country);
        when(holidayRepository.findByYearAndCountryCode(year, countryCode)).thenReturn(holidays);

        when(externalHolidayClient.getHolidays(year, countryCode)).thenReturn(externalHolidays);
        when(holidayRepository.findByDateAndCountryAndNameAndIsGlobal(
                LocalDate.parse("2025-02-17"), country, "Family Day", false
        ))
                .thenReturn(Optional.empty());

        holidayService.updateHolidays(year, countryCode);

        verify(holidayRepository).findByYearAndCountryCode(year, countryCode);
        verify(holidayRepository).deleteAll(holidays);

        verify(externalHolidayClient).getHolidays(year, countryCode);
        verify(holidayRepository).findByDateAndCountryAndNameAndIsGlobal(
                LocalDate.parse("2025-02-17"), country, "Family Day", false
        );
        verify(holidayRepository).save(any(Holiday.class));
    }

}