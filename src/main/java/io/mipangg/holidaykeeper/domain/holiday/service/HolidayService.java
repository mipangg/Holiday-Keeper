package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final ExternalHolidayClient externalHolidayClient;
    private final CountryService countryService;

    public void syncHolidays() {
        List<Integer> years = getYears();
        Map<String, Country> countryMap = countryService.findAll();
        for (Integer year : years) {
            for (String countryCode : countryMap.keySet()) {
                getExternalHolidays(year, countryCode, countryMap.get(countryCode));
            }
        }
    }

    // 각각의 ExternalHolidayResponse를 처리
    private void getExternalHolidays(Integer year, String countryCode, Country country) {
        List<ExternalHolidayResponse> externalHolidays =
                externalHolidayClient.getHolidays(year, countryCode);
        for (ExternalHolidayResponse externalHoliday : externalHolidays) {
            Holiday holiday = saveHolidayIfNotExists(externalHoliday, country);
            // TODO: County, CountryType, HolidayCounty 저장

            List<String> types = externalHoliday.types();

        }
    }

    private Holiday saveHolidayIfNotExists(ExternalHolidayResponse externalHoliday, Country country) {
        LocalDate date = LocalDate.parse(externalHoliday.date());
        Optional<Holiday> holidayOptional = holidayRepository.findByDateAndCountry(date, country);
        if (holidayOptional.isPresent()) {
            return holidayOptional.get();
        }

        return holidayRepository.save(
                Holiday.builder()
                        .date(date)
                        .localName(externalHoliday.localName())
                        .name(externalHoliday.name())
                        .isFixed(externalHoliday.fixed())
                        .isGlobal(externalHoliday.global())
                        .launchYear(externalHoliday.launchYear())
                        .country(country)
                        .build()
        );
    }

    // 현재 연도로 부터 최근 5년의 연도를 구한 후 반환
    private List<Integer> getYears() {
        List<Integer> years = new ArrayList<>();

        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 6; i++) {
            years.add(currentYear - i);
        }

        return years;
    }
}
