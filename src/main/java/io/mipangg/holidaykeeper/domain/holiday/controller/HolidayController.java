package io.mipangg.holidaykeeper.domain.holiday.controller;

import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @ApiResponse(responseCode = "400", description = "invalid value")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHolidays() {
        holidayService.syncHolidays();
    }

    @Operation(summary = "삭제")
    @ApiResponse(responseCode = "204", description = "특정 연도의 국가 공휴일 목록 삭제 성공")
    @ApiResponse(responseCode = "400", description = "invalid value")
    @ApiResponse(responseCode = "404", description = "not found")
    @DeleteMapping("/{year}/{countryCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHolidays(
            @PathVariable @Positive int year,
            @PathVariable String countryCode
    ) {
        holidayService.deleteHolidays(year, countryCode);
    }

}
