package io.mipangg.holidaykeeper.domain.country.service;

import io.mipangg.holidaykeeper.common.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.common.service.ExternalApiClient;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
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

    // 외부 api에서 country 정보를 조회 -> 중복검사 후 저장
    @Transactional
    public void saveCountries() {
        List<ExternalCountryResponse> externalCountries = externalApiClient.getExternalCountries();
        for (ExternalCountryResponse countryResp : externalCountries) {
            String countryCode = countryResp.countryCode();
            String name = countryResp.name();

            if (!countryRepository.existsByCountryCodeAndName(countryCode, name)) {
                Country country = Country.builder()
                        .countryCode(countryCode)
                        .name(name)
                        .build();
                countryRepository.save(country);
            }
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Country> getAll() {
        Map<String, Country> countryMap = new HashMap<>();
        List<Country> countries = countryRepository.findAll();

        if (countries.isEmpty()) {
            throw new IllegalArgumentException("현재 저장된 CountryCode가 없습니다.");
        }

        for (Country country : countries) {
            countryMap.put(country.getCountryCode(), country);
        }

        return countryMap;
    }


}
