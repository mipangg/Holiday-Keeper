package io.mipangg.holidaykeeper.domain.country.service;

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

    public Map<String, Country> findAll() {
        Map<String, Country> allCountries = new HashMap<>();

        countryRepository.findAll().forEach(country -> {
            allCountries.put(country.getCode(), country);
        });

        return allCountries;
    }

}
