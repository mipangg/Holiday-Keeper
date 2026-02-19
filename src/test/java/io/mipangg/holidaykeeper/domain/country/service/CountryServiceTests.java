package io.mipangg.holidaykeeper.domain.country.service;

import static io.mipangg.holidaykeeper.util.TestUtil.genCountryBrazil;
import static io.mipangg.holidaykeeper.util.TestUtil.genCountryCanada;
import static io.mipangg.holidaykeeper.util.TestUtil.genCountryKorea;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.external.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import java.util.List;
import java.util.Map;
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

        when(countryRepository.existsBy()).thenReturn(Boolean.FALSE);
        when(externalApiClient.getExternalCountries()).thenReturn(externalCountryResponses);

        // List<ExternalCountryResponse> -> Map<String, Country>로 변환
        Map<String, Country> expected = Map.of(
                "BR", genCountryBrazil(),
                "CA", genCountryCanada(),
                "KR", genCountryKorea()
        );

        // countryRepository.saveAll(List<Country>)로 한번에 저장
        Map<String, Country> actual = countryService.saveCountries();

        verify(countryRepository).existsBy();
        verify(countryRepository).saveAll(anyCollection());

        assertThat(actual).hasSize(expected.size());
        assertThat(actual.get("BR").getCountryCode()).isEqualTo(expected.get("BR").getCountryCode());
        assertThat(actual.get("BR").getName()).isEqualTo(expected.get("BR").getName());
        assertThat(actual.get("CA").getCountryCode()).isEqualTo(expected.get("CA").getCountryCode());
        assertThat(actual.get("CA").getName()).isEqualTo(expected.get("CA").getName());
        assertThat(actual.get("KR").getCountryCode()).isEqualTo(expected.get("KR").getCountryCode());
        assertThat(actual.get("KR").getName()).isEqualTo(expected.get("KR").getName());

    }

    @Test
    @DisplayName("국가 목록을 저장하려 할 때 country repo가 비어있지 않으면 409가 발생하는지 테스트")
    void saveCountries409FailTest() {

        when(countryRepository.existsBy()).thenReturn(Boolean.TRUE);

        // 중복 검사?? 두 번 호출 후 저장 데이터 확인해보기
        // countryRepository가 비어 있지 않으면 409 예외 발생
        // create 기능은 "데이터 적재"이기 때문에 빈 저장소 -> 데이터 저장만 가능
        assertThatThrownBy(
                () -> {
                    countryService.saveCountries();
                }
        ).isInstanceOf(CustomLogicException.class)
                .hasMessage("이미 존재하는 데이터입니다.");
    }

}