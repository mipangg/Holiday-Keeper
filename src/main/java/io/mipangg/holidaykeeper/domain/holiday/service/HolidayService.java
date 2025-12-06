package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.domain.holidayType.service.HolidayTypeService;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final ExternalHolidayClient externalHolidayClient;

    private final CountryService countryService;
    private final HolidayTypeService holidayTypeService;
    private final HolidayCountyService holidayCountyService;

    @Transactional
    public void syncHolidays() {
        List<Integer> years = getYears();
        Map<String, Country> countryMap = countryService.findAll();
        for (Integer year : years) {
            for (String countryCode : countryMap.keySet()) {
                getExternalHolidays(year, countryCode, countryMap.get(countryCode));
            }
        }
    }

    @Transactional
    public void deleteHolidays(int year, String countryCode) {
        // TODO: holiday, HolidayType, holidayCounty 삭제

        
    }

    // 각각의 ExternalHolidayResponse를 처리
    private void getExternalHolidays(Integer year, String countryCode, Country country) {
        List<ExternalHolidayResponse> externalHolidays =
                externalHolidayClient.getHolidays(year, countryCode);
        for (ExternalHolidayResponse externalHoliday : externalHolidays) {
            Holiday holiday = saveHolidayIfNotExists(externalHoliday, country);

            holidayCountyService.saveIfNotExists(externalHoliday.counties(), country, holiday);
            holidayTypeService.saveIfNotExists(externalHoliday.types(), holiday);
        }
    }

    private Holiday saveHolidayIfNotExists(
            ExternalHolidayResponse externalHoliday,
            Country country
    ) {
        LocalDate date = LocalDate.parse(externalHoliday.date());
        return holidayRepository.findByDateAndCountry(date, country)
                .orElseGet(() ->
                        holidayRepository.save(
                                Holiday.builder()
                                        .date(date)
                                        .localName(externalHoliday.localName())
                                        .name(externalHoliday.name())
                                        .isFixed(externalHoliday.fixed())
                                        .isGlobal(externalHoliday.global())
                                        .launchYear(externalHoliday.launchYear())
                                        .country(country)
                                        .build()
                        )
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
