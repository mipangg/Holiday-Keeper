package io.mipangg.holidaykeeper.domain.country.service;

import io.mipangg.holidaykeeper.external.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import io.mipangg.holidaykeeper.global.exception.ErrorCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountryService {
    
    private final CountryRepository countryRepository;

    private final ExternalApiClient externalApiClient;
    
    @Transactional
    public Map<String, Country> saveCountries() {

        if (countryRepository.existsBy()) {
            throw new CustomLogicException(ErrorCode.CONFLICT, "Country 테이블에 이미 데이터가 존재합니다.");
        }

        List<ExternalCountryResponse> externalCountries = externalApiClient.getExternalCountries();

        Map<String, Country> countries = new HashMap<>();
        for (ExternalCountryResponse countryResponse : externalCountries) {
            countries.put(
                    countryResponse.countryCode(),
                    Country.builder()
                            .countryCode(countryResponse.countryCode())
                            .name(countryResponse.name())
                            .build()
            );
        }

        countryRepository.saveAll(countries.values());
        return countries;
    }

    @Transactional(readOnly = true)
    public Country getCountryByCountryCode(String countryCode) {
        return countryRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new CustomLogicException(
                        ErrorCode.NOT_FOUND,
                        "countryCode와 일치하는 국가를 찾을 수 없습니다.")
                );
    }

}
