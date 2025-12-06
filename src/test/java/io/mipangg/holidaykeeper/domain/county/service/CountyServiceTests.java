package io.mipangg.holidaykeeper.domain.county.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.repository.CountyRepository;
import java.util.List;
import java.util.Optional;
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
    @DisplayName("이미 저장된 County가 없으면 저장한다")
    void saveIfNotExists_success_Test() {

        List<String> counties = List.of(
                "CA-AB",
                "CA-BC",
                "CA-NB",
                "CA-ON",
                "CA-SK"
        );
        Country country = Country.builder()
                .code("CA")
                .name("Canada")
                .build();

        when(countyRepository.findByName(anyString())).thenReturn(Optional.empty());

        countyService.saveIfNotExists(counties, country);

        verify(countyRepository, times(5)).findByName(anyString());
        verify(countyRepository, times(5)).save(any(County.class));

    }

    @Test
    @DisplayName("동일한 county가 이미 있으면 저장하지 않는다")
    void saveIfNotExists_test_case_county_already_exists() {

        Country canada = Country.builder()
                .code("CA")
                .name("Canada")
                .build();
        County county = County.builder()
                .name("CA-AB")
                .country(canada)
                .build();

        when(countyRepository.findByName(anyString())).thenReturn(Optional.of(county));

        countyService.saveIfNotExists(List.of("CA-AB"), canada);

        verify(countyRepository, times(1)).findByName("CA-AB");
        verify(countyRepository, never()).save(any(County.class));

    }

}