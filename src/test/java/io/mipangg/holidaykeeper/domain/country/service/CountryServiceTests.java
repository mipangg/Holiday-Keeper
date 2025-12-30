package io.mipangg.holidaykeeper.domain.country.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.common.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.common.service.ExternalApiClient;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
import java.util.Collections;
import java.util.List;
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
    private ExternalApiClient externalApiClient;

    @Test
    @DisplayName("외부 api에서 모든 country 정보를 조회하여 중복 검사 후 저장할 수 있다")
    void saveCountries_success() {

        List<ExternalCountryResponse> externalCountries = List.of(
                new ExternalCountryResponse("BR", "Brazil"),
                new ExternalCountryResponse("CA", "Canada"),
                new ExternalCountryResponse("KR", "South Korea")
        );

        when(externalApiClient.getExternalCountries()).thenReturn(externalCountries);
        when(countryRepository.existsByCountryCodeAndName("BR", "Brazil"))
                .thenReturn(Boolean.FALSE);
        when(countryRepository.existsByCountryCodeAndName("CA", "Canada"))
                .thenReturn(Boolean.FALSE);
        when(countryRepository.existsByCountryCodeAndName("KR", "South Korea"))
                .thenReturn(Boolean.TRUE);

        countryService.saveCountries();

        verify(countryRepository).existsByCountryCodeAndName("BR", "Brazil");
        verify(countryRepository).existsByCountryCodeAndName("CA", "Canada");
        verify(countryRepository).existsByCountryCodeAndName("KR", "South Korea");
        verify(countryRepository, times(2)).save(any(Country.class));

    }

    @Test
    @DisplayName("모든 countryCode 리스트를 반환할 수 있다")
    void getAllCountryCodes_success() {

        List<String> expected = List.of("BR", "CA", "KR");

        when(countryRepository.findAllCountryCodes()).thenReturn(expected);

        List<String> actual = countryService.getAllCountryCodes();

        assertThat(actual).hasSize(expected.size());
        assertThat(actual).isEqualTo(expected);

        verify(countryRepository).findAllCountryCodes();

    }

    @Test
    @DisplayName("countryRepository에서 반환된 countryCode 리스트가 빈 리스트면 예외가 발생한다")
    void getAllCountry_fail() {

        when(countryRepository.findAllCountryCodes()).thenReturn(Collections.emptyList());

        assertThatThrownBy(
                () -> {
                    countryService.getAllCountryCodes();
                }
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 저장된 CountryCode가 없습니다.");

    }
}