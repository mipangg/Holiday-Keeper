package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.util.TestUtils.getCountiesCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getCountryCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getCountyStrsCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getHolidayCanada;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.service.CountyService;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayCountyRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    @DisplayName("HolidayCounty를 중복되지 않게 저장한다")
    void saveIfNotExists_success_test() {

        List<String> countyStrs = getCountyStrsCanada();
        Country canada = getCountryCanada();
        List<County> counties = getCountiesCanada();
        Holiday holiday = getHolidayCanada();

        when(countyService.saveIfNotExists(countyStrs, canada)).thenReturn(counties);
        when(holidayCountyRepository.findByCountyAndHoliday(any(County.class), any(Holiday.class)))
                .thenReturn(Optional.empty());

        holidayCountyService.saveIfNotExists(countyStrs, canada, holiday);

        verify(holidayCountyRepository, times(5))
                .findByCountyAndHoliday(any(County.class), any(Holiday.class));

        verify(holidayCountyRepository, times(5))
                .save(any(HolidayCounty.class));

    }

    @Test
    @DisplayName("HolidayCounty가 이미 있는 경우 저장하지 않는다")
    void saveIfNotExists_test_case_holidayCounty_already_exists() {

        List<String> countyStrs = List.of("CA-AB");
        Country canada = getCountryCanada();
        County county = getCountiesCanada().getFirst(); // CA-AB
        Holiday holiday = getHolidayCanada();
        HolidayCounty holidayCounty = HolidayCounty.builder()
                .county(county)
                .holiday(holiday)
                .build();

        when(countyService.saveIfNotExists(countyStrs, canada)).thenReturn(List.of(county));
        when(holidayCountyRepository.findByCountyAndHoliday(any(County.class), any(Holiday.class)))
                .thenReturn(Optional.of(holidayCounty));

        holidayCountyService.saveIfNotExists(countyStrs, canada, holiday);

        verify(holidayCountyRepository, times(1))
                .findByCountyAndHoliday(any(County.class), any(Holiday.class));
        verify(holidayCountyRepository, never()).save(any(HolidayCounty.class));

    }

    @Test
    @DisplayName("holidays를 포함하고 있는 holidayCounty들을 삭제한다")
    void deleteHolidayCounties_success_test() {

        Country canada = getCountryCanada();

        Holiday holiday = Holiday.builder()
                .date(LocalDate.parse("2025-02-17"))
                .localName("Louis Riel Day")
                .name("Louis Riel Day")
                .country(canada)
                .isFixed(false)
                .isGlobal(false)
                .launchYear(null)
                .build();

        County county = County.builder()
                .name("CA-MB")
                .country(canada)
                .build();

        HolidayCounty holidayCounty = HolidayCounty.builder()
                .holiday(holiday)
                .county(county)
                .build();

        when(holidayCountyRepository.findByHoliday(holiday))
                .thenReturn(List.of(holidayCounty));

        holidayCountyService.deleteHolidayCounties(List.of(holiday));

        verify(holidayCountyRepository, times(1)).findByHoliday(holiday);
        verify(holidayCountyRepository, times(1))
                .deleteAll(List.of(holidayCounty));

    }
}