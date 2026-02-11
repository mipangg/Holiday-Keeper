package io.mipangg.holidaykeeper.domain.country.service;

import io.mipangg.holidaykeeper.external.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import io.mipangg.holidaykeeper.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountryService {
    
    private final CountryRepository countryRepository;

    private final ExternalApiClient externalApiClient;
    
    @Transactional
    public List<Country> saveCountries() {

        if (countryRepository.count() > 0L) {
            throw new CustomLogicException(ErrorCode.CONFLICT, "Country 테이블에 이미 데이터가 존재합니다.");
        }

        List<ExternalCountryResponse> externalCountries = externalApiClient.getExternalCountries();

        List<Country> countries = new ArrayList<>();
        for (ExternalCountryResponse countryResponse : externalCountries) {
            countries.add(
                    Country.builder()
                            .countryCode(countryResponse.countryCode())
                            .name(countryResponse.name())
                            .build()
            );
        }

        countryRepository.saveAll(countries);
        return countries;
    }

}
