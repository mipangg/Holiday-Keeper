package io.mipangg.holidaykeeper.domain.county.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.repository.CountyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountyService {

    private final CountyRepository countyRepository;

    @Transactional
    public void saveIfNotExists(List<String> counties, Country country) {
        for (String county : counties) {
            countyRepository.findByName(county).orElseGet(() ->
                    countyRepository.save(
                            County.builder()
                                    .name(county)
                                    .country(country)
                                    .build()
                    )
            );
        }
    }

}
