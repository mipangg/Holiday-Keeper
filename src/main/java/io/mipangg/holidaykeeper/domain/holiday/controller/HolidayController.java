package io.mipangg.holidaykeeper.domain.holiday.controller;

import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
@Tag(name = "Holiday", description = "공휴일 관리 Api")
public class HolidayController {

    private final HolidayService holidayService;

    @Operation(summary = "데이터 적재")
    @ApiResponse(responseCode = "201", description = "공휴일 데이터 저장 성공")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHolidays() {
        holidayService.syncHolidays();
    }

}
