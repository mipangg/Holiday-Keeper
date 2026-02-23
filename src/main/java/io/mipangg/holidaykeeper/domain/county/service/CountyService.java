package io.mipangg.holidaykeeper.domain.county.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.county.repository.CountyRepository;
import io.mipangg.holidaykeeper.domain.holidayCounty.dto.CountyElemDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountyService {

    private final CountyRepository countyRepository;

    // key: countyName
    @Transactional
    public Map<String, County> getOrCreateCounties(Set<CountyElemDto> countyDtos) {
        Set<String> countyNames = new HashSet<>();
        countyDtos.forEach(dto -> countyNames.addAll(dto.countyNames()));
        Map<String, County> counties = new HashMap<>();
        List<County> newCounties = new ArrayList<>();

        List<County> existingCounties = countyRepository.findByCountyIn(countyNames);
        if (existingCounties != null && !existingCounties.isEmpty()) {
            existingCounties.forEach(county -> counties.put(county.getCounty(), county));
        }

        countyDtos.forEach(dto -> {
            Country country = dto.country();
            dto.countyNames().forEach(countyName -> {
               County county = counties.get(countyName);
               if (county == null) {
                   county = County.builder()
                           .country(country)
                           .county(countyName)
                           .build();
                   counties.put(countyName, county);
                   newCounties.add(county);
               }
            });
        });

        if (!newCounties.isEmpty()) {
            countyRepository.saveAll(newCounties);
        }

        return counties;
    }

}
