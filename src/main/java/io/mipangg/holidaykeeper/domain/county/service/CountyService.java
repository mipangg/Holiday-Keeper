package io.mipangg.holidaykeeper.domain.county.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.repository.CountyRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountyService {

    private final CountyRepository countyRepository;

    @Transactional
    public List<County> saveIfNotExists(List<String> counties, Country country) {
        List<County> savedCounties = new ArrayList<>();
        for (String county : counties) {
            County savedCounty = countyRepository.findByName(county).orElseGet(() ->
                    countyRepository.save(
                            County.builder()
                                    .name(county)
                                    .country(country)
                                    .build()
                    )
            );
            savedCounties.add(savedCounty);
        }
        return savedCounties;
    }

    @Transactional
    public County findOrCreate(String name, Country country) {
        return countyRepository.findByNameAndCountry(name, country)
                .orElseGet(() ->
                        countyRepository.save(
                                County.builder()
                                        .name(name)
                                        .country(country)
                                        .build()
                        )
                );
    }

}
