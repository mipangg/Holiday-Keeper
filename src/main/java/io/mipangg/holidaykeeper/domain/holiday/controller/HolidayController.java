package io.mipangg.holidaykeeper.domain.holiday.controller;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;
    private final CountryService countryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createHolidays() {
        Map<String, Country> countries = countryService.saveCountries();
        holidayService.saveHolidays(countries);
    }

    @DeleteMapping("/{year}/{countryCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHolidays(
            @PathVariable @Positive @NotNull Integer year,
            @PathVariable @NotBlank String countryCode
    ) {
        holidayService.deleteHolidays(year, countryCode);
    }


}
