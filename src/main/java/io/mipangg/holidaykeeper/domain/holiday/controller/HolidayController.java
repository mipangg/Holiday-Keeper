package io.mipangg.holidaykeeper.domain.holiday.controller;

import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHolidays() {
        holidayService.saveHolidaysInLastFiveYears();
    }

}
