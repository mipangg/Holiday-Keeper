package io.mipangg.holidaykeeper.domain.country.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    @DisplayName("모든 countryCode 리스트를 조회한다")
    void getAllCountryCodes_success_test() {

        List<String> countryCodes = List.of("BR", "CA", "KR", "JP");

        when(countryRepository.findAllCodes()).thenReturn(countryCodes);

        List<String> resp = countryService.getAllCountryCodes();

        verify(countryRepository, times(1)).findAllCodes();
        assertThat(resp).containsExactlyInAnyOrder("BR", "CA", "KR", "JP");

    }

    @Test
    @DisplayName("모든 countryCode 리스트 조회 시 빈 리스트가 반환되면 에러가 발생한다")
    void getAllCountryCodes_fail_test() {

        when(countryRepository.findAllCodes()).thenReturn(List.of());

        assertThatThrownBy(
                () -> {
                    countryService.getAllCountryCodes();
                }
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("빈 Country code list 입니다.");

    }
}