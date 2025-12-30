package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.common.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.common.service.ExternalApiClient;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidaySupportService {

    private ExternalApiClient externalApiClient;
    private CountryService countryService;

    // countryService의 저장 메서드 호출 후 저장된 country 맵 반환
    public Map<String, Country> saveAndGetCountries() {
        countryService.saveCountries();
        return countryService.getAll();
    }

    // 최근 5년의 연도가 담긴 리스트 반환
    public List<Integer> getLastFiveYears() {
        List<Integer> lastFiveYears = new ArrayList<>();

        int thisYear = LocalDate.now().getYear();
        for (int i = 0; i < 5; i++) {
            lastFiveYears.add(thisYear - i);
        }

        return lastFiveYears;
    }

    public List<ExternalHolidayResponse> getExternalHolidayResps(
            List<Integer> years, Set<String> countryCodes
    ) {
        List<ExternalHolidayResponse> externalHolidayResps = new ArrayList<>();

        for (Integer year : years) {
            for (String countryCode : countryCodes) {
                externalHolidayResps.addAll(
                        externalApiClient.getExternalHolidays(year, countryCode)
                );
            }
        }

        return externalHolidayResps;
    }

}
