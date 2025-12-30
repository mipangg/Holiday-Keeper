package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.common.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;

    private final HolidaySupportService holidaySupportService;

    @Transactional
    public void saveHolidaysInLastFiveYears() {
        Map<String, Country> countryMap = holidaySupportService.saveAndGetCountries();
        List<Integer> lastFiveYears = holidaySupportService.getLastFiveYears();

        List<ExternalHolidayResponse> externalHolidayResps =
                holidaySupportService.getExternalHolidayResps(lastFiveYears, countryMap.keySet());

        for (ExternalHolidayResponse externalHolidayResp : externalHolidayResps) {
            LocalDate date = externalHolidayResp.date();
            String name = externalHolidayResp.name();
            Country country = countryMap.get(externalHolidayResp.countryCode());
            if (!holidayRepository.existsByDateAndNameAndCountry(date, name, country)) {
                holidayRepository.save(
                        Holiday.builder()
                                .date(date)
                                .localName(externalHolidayResp.localName())
                                .name(name)
                                .launchYear(externalHolidayResp.launchYear())
                                .fixed(externalHolidayResp.fixed())
                                .global(externalHolidayResp.global())
                                .country(country)
                                .build()
                );
                // holidayType, holidayCounty 추가 필요
            }
        }

    }
}
