package io.mipangg.holidaykeeper.domain.holiday.controller;

import io.mipangg.holidaykeeper.domain.common.PageResponse;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayListReadResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayReadRequest;
import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;
    private final CountryService countryService;

    @Operation(summary = "데이터 적재")
    @ApiResponse(responseCode = "201", description = "국가, 공휴일 데이터 저장 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 데이터")
    @ApiResponse(responseCode = "500", description = "외부 API 호출 실패")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createHolidays() {
        Map<String, Country> countries = countryService.saveCountries();
        holidayService.saveHolidays(countries);
    }

    @Operation(summary = "특정 연도·국가의 공휴일 레코드 전체 삭제")
    @ApiResponse(responseCode = "204", description = "특정 연도·국가의 공휴일 레코드 전체 삭제 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "404", description = "특정 연도·국가의 공휴일을 찾을 수 없음")
    @DeleteMapping("/{year}/{countryCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHolidays(
            @PathVariable @Positive @NotNull Integer year,
            @PathVariable @NotBlank String countryCode
    ) {
        holidayService.deleteHolidays(year, countryCode);
    }

    @Operation(summary = "특정 연도·국가의 공휴일 목록 조회")
    @ApiResponse(responseCode = "200", description = "특정 연도·국가의 공휴일 목록 조회 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 날짜로 조회 요청")
    @GetMapping("/{year}/{countryCode}")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<HolidayListReadResponse> readHolidays(
            @PathVariable @Positive @NotNull Integer year,
            @PathVariable @NotBlank String countryCode,
            @Valid @ParameterObject HolidayReadRequest request
    ) {
        return holidayService.searchHolidays(year, countryCode, request);
    }

    @Operation(summary = "특정 연도·국가의 공휴일 목록 재동기화")
    @ApiResponse(responseCode = "200", description = "특정 연도·국가의 공휴일 목록 재동기화 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @PutMapping("/{year}/{countryCode}")
    @ResponseStatus(HttpStatus.OK)
    public void upsertHolidays(
            @PathVariable @Positive @NotNull Integer year,
            @PathVariable @NotBlank String countryCode
    ) {
        holidayService.upsertHolidays(year, countryCode);
    }


}
