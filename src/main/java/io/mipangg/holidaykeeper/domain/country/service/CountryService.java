package io.mipangg.holidaykeeper.domain.country.service;

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
    private final ExternalCountryClient externalCountryClient;

    @Transactional
    public void syncCountries() {
        externalCountryClient.getCountries().forEach(externalCountry -> {
            if (countryRepository.existsByCode(externalCountry.countryCode())) {
                return;
            }

            Country country = Country.builder()
                    .code(externalCountry.countryCode())
                    .name(externalCountry.name())
                    .build();

            countryRepository.save(country);
        });
    }

    public List<String> getAllCountryCodes() {
        List<String> allCountryCodes = countryRepository.findAllCodes();

        if (allCountryCodes.isEmpty()) {
            // TODO: exception 추가 처리 필요
            throw new IllegalArgumentException("빈 Country code list 입니다.");
        }

        return allCountryCodes;
    }

}
