package io.mipangg.holidaykeeper.domain.country.service;

import io.mipangg.holidaykeeper.common.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.common.service.ExternalApiClient;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.repository.CountryRepository;
import java.util.List;
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


}
