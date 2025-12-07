package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.util.TestUtils.getCountiesCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getCountryCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getCountyStrsCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getHolidayCanada;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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
    @DisplayName("HolidayCounty를 upsert 할 수 있다")
    void upsertHolidayCounties_insert_success_test() {

        Holiday holiday = getHolidayCanada();
        Country country = getCountryCanada();

        List<String> externalCountyNames = List.of("CA-AB");
        County countyAB = getCountiesCanada().getFirst();

        when(holidayCountyRepository.findByHoliday(holiday)).thenReturn(List.of());
        when(countyService.findOrCreate("CA-AB", country)).thenReturn(countyAB);

        holidayCountyService.upsertHolidayCounties(holiday, externalCountyNames, country);

        verify(holidayCountyRepository, times(1)).saveAll(anyList());
        verify(holidayCountyRepository, never()).deleteAll(anyList());
        verify(countyService, times(1)).findOrCreate("CA-AB", country);
        verify(holidayCountyRepository, never()).deleteByHoliday(anyLong());

    }
}