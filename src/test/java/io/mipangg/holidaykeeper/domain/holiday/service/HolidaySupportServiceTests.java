package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.util.TestUtils.getCountryMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
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
class HolidaySupportServiceTests {

    @InjectMocks
    private HolidaySupportService holidaySupportService;

    @Mock
    private CountryService countryService;

    @Test
    @DisplayName("countryService의 저장 메서드 호출 후 저장된 country 맵 반환을 반환할 수 있다")
    void saveCountriesAndGetCountryCodes_success() {

        Map<String, Country> countryMap = getCountryMap();

        when(countryService.getAll()).thenReturn(countryMap);

        Map<String, Country> result = holidaySupportService.saveAndGetCountries();

        assertThat(result).isEqualTo(countryMap);

        verify(countryService).saveCountries();
        verify(countryService).getAll();

    }

    @Test
    @DisplayName("최근 5년의 연도가 담긴 리스트를 반환할 수 있다")
    void getLastFiveYears_success() {

        int thisYear = LocalDate.now().getYear();
        List<Integer> expected = List.of(
                thisYear, thisYear - 1, thisYear - 2, thisYear - 3, thisYear - 4
        );

        List<Integer> actual = holidaySupportService.getLastFiveYears();

        assertThat(actual).isEqualTo(expected);

    }

}