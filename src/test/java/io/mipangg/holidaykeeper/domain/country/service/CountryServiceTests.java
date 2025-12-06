package io.mipangg.holidaykeeper.domain.country.service;

import static io.mipangg.holidaykeeper.util.TestUtils.getCountries;
import static io.mipangg.holidaykeeper.util.TestUtils.getCountryCanada;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
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
class CountryServiceTests {

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ExternalCountryClient externalCountryClient;

    @Test
    @DisplayName("externalCountryClient로 불러온 country 정보들을 repository에 저장한다")
    void syncCountries_success_test() {

        List<ExternalCountryResponse> externalCountryResps = List.of(
                new ExternalCountryResponse("AD", "Andorra"),
                new ExternalCountryResponse("ZW", "Zimbabwe")
        );

        when(externalCountryClient.getCountries()).thenReturn(externalCountryResps);
        when(countryRepository.existsByCode(anyString())).thenReturn(false);

        countryService.syncCountries();

        verify(countryRepository, times(2)).save(any());

    }

    @Test
    @DisplayName("모든 country 정보를 map 형태로 반환한다")
    void findAll_success_test() {

        List<Country> countries = getCountries();

        when(countryRepository.findAll()).thenReturn(countries);

        Map<String, Country> allCountries = countryService.findAll();

        verify(countryRepository, times(1)).findAll();

        assertThat(allCountries.get("BR")).isEqualTo(countries.get(0));
        assertThat(allCountries.get("CA")).isEqualTo(countries.get(1));
        assertThat(allCountries.get("KR")).isEqualTo(countries.get(2));

    }

    @Test
    @DisplayName("code로 특정 country를 찾아 반환한다")
    void getByCode_success_test() {

        Country country = getCountryCanada();
        String code = "CA";

        when(countryRepository.findByCode(code)).thenReturn(Optional.of(country));

        Country resultCountry = countryService.getByCode(code);

        assertThat(resultCountry.getCode()).isEqualTo(code);
        assertThat(resultCountry.getName()).isEqualTo("Canada");

    }

    @Test
    @DisplayName("code와 일치하는 country가 없으면 예외가 발생한다")
    void getByCode_fail_test() {

        String code = "AA";

        when(countryRepository.findByCode(code)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> {
                    countryService.getByCode(code);
                }
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("%s를 코드로 가진 country를 찾을 수 없습니다.", code));
    }
}