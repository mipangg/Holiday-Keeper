package io.mipangg.holidaykeeper.domain.county.service;


import static io.mipangg.holidaykeeper.util.TestUtil.genCountryCanada;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.repository.CountyRepository;
import io.mipangg.holidaykeeper.domain.holidayCounty.dto.CountyElemDto;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CountyServiceTests {

    @InjectMocks
    private CountyService countyService;

    @Mock
    private CountyRepository countyRepository;

    @Test
    @DisplayName("county가 이미 존재하면 반환하고 없으면 저장 후 반환하는 기능 테스트")
    void getOrCreateCountiesTest() {

        Country canada = genCountryCanada();
        Set<CountyElemDto> countyDtos = Set.of(
                new CountyElemDto(
                        canada,
                        List.of(
                                "CA-NS"
                        )
                ),
                new CountyElemDto(
                        canada,
                        List.of(
                                "CA-AB",
                                "CA-BC",
                                "CA-NS",
                                "CA-SK"
                        )
                )
        );
        List<County> existingCounties = List.of(
                County.builder()
                        .country(canada)
                        .county("CA-AB")
                        .build()
        );


        when(countyRepository.findByCountyIn(anySet())).thenReturn(existingCounties);

        Map<String, County> result = countyService.getOrCreateCounties(countyDtos);

        verify(countyRepository).findByCountyIn(anySet());
        verify(countyRepository).saveAll(anyList());

        assertThat(result.get("CA-AB").getCounty()).isEqualTo("CA-AB");
        assertThat(result.get("CA-BC").getCounty()).isEqualTo("CA-BC");
        assertThat(result.get("CA-SK").getCounty()).isEqualTo("CA-SK");
        assertThat(result.get("CA-NS").getCounty()).isEqualTo("CA-NS");
        assertThat(result.get("CA-AB").getCountry().getName()).isEqualTo("Canada");
        assertThat(result.get("CA-BC").getCountry().getName()).isEqualTo("Canada");
        assertThat(result.get("CA-SK").getCountry().getName()).isEqualTo("Canada");
        assertThat(result.get("CA-NS").getCountry().getName()).isEqualTo("Canada");

    }

}