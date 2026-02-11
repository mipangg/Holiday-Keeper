package io.mipangg.holidaykeeper.domain.country.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.external.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
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
    @DisplayName("국가 목록을 저장하고 저장된 국가 목록을 반환하는지 테스트")
    void saveCountriesTest() {

        // getExternalCountries 호출 하여 List<ExternalCountryResponse> 조회
        List<ExternalCountryResponse> externalCountryResponses= List.of(
                new ExternalCountryResponse("BR", "Brazil"),
                new ExternalCountryResponse("CA", "Canada"),
                new ExternalCountryResponse("KR", "South Korea")
        );

        when(externalApiClient.getExternalCountries()).thenReturn(externalCountryResponses);

        // List<ExternalCountryResponse> -> List<Country>로 변환
        List<Country> expected = List.of(
                Country.builder().countryCode("BR").name("Brazil").build(),
                Country.builder().countryCode("CA").name("Canada").build(),
                Country.builder().countryCode("KR").name("South Korea").build()
        );

        // countryRepository.saveAll(List<Country>)로 한번에 저장
        List<Country> actual = countryService.saveCountries();

        verify(countryRepository).saveAll(anyList());

        assertThat(actual).hasSize(expected.size());
        assertThat(actual.getFirst().getCountryCode()).isEqualTo(expected.getFirst().getCountryCode());
        assertThat(actual.getFirst().getName()).isEqualTo(expected.getFirst().getName());
        assertThat(actual.getLast().getCountryCode()).isEqualTo(expected.getLast().getCountryCode());
        assertThat(actual.getLast().getName()).isEqualTo(expected.getLast().getName());

    }

    @Test
    @DisplayName("국가 목록을 저장하려 할 때 country repo가 비어있지 않으면 409가 발생하는지 테스트")
    void saveCountries409FailTest() {

        when(countryRepository.count()).thenReturn(3L);

        // 중복 검사?? 두 번 호출 후 저장 데이터 확인해보기
        // countryRepository가 비어 있지 않으면 409 예외 발생
        // create 기능은 "데이터 적재"이기 때문에 빈 저장소 -> 데이터 저장만 가능
        assertThatThrownBy(
                () -> {
                    countryService.saveCountries();
                }
        ).isInstanceOf(CustomLogicException.class);
    }

}