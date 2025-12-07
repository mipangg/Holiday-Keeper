package io.mipangg.holidaykeeper.config;

import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HolidayBatchInitializer {

    private final CountryService countryService;
    private final HolidayService holidayService;

    @PostConstruct
    public void loadInitialData() {

        countryService.syncCountries();
        holidayService.syncHolidays();

    }

}
