package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidaySupportService {

    private CountryService countryService;

    // countryService의 저장 메서드 호출 후 저장된 countryCode 리스트 반환
    public List<String> saveCountriesAndGetCountryCodes() {
        countryService.saveCountries();
        return countryService.getAllCountryCodes();
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

}
