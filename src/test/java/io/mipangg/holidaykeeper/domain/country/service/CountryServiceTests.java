package io.mipangg.holidaykeeper.domain.country.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.common.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.common.service.ExternalApiClient;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
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
}