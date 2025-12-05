package io.mipangg.holidaykeeper.domain.country.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.dto.ExternalCountryResponse;
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

}