package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.common.PageResponse;
import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayCountiesDto;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayCreationResultDto;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayListReadResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayReadRequest;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayTypesDto;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.properties.HolidayProperties;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.domain.holidayCounty.service.HolidayCountyService;
import io.mipangg.holidaykeeper.domain.holidayType.service.HolidayTypeService;
import io.mipangg.holidaykeeper.external.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import io.mipangg.holidaykeeper.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;

    private final ExternalApiClient externalApiClient;
    private final HolidayCountyService holidayCountyService;
    private final HolidayTypeService holidayTypeService;

    private final HolidayProperties holidayProperties;

    @Transactional
    public void saveHolidays(Map<String, Country> countries) {
        if (holidayRepository.existsBy()) {
            throw new CustomLogicException(
                    ErrorCode.CONFLICT,
                    "Holiday 테이블에 이미 데이터가 존재합니다."
            );
        }

        HolidayCreationResultDto holidayCreationResultDto =
                prepareHolidayCreationResult(countries, getYears());
        Map<String, Holiday> holidayMap = new HashMap<>();
        holidayCreationResultDto.holidays().forEach(holiday ->
                holidayMap.put(
                        createUniqueKey(
                                holiday.getDate(),
                                holiday.getLocalName(),
                                holiday.getCountry().getCountryCode()
                        ),
                        holiday
                )
        );

        holidayRepository.saveAll(holidayCreationResultDto.holidays());

        holidayTypeService.saveHolidayTypes(holidayCreationResultDto.holidayTypesDtos());
        holidayCountyService.saveHolidayCounties(
                holidayCreationResultDto.holidayCountiesDtos(),
                holidayMap
        );

    }

    @Transactional
    public void deleteHolidays(Integer year, String countryCode) {
        List<Holiday> targetHolidays = holidayRepository
                .findByYearAndCountryCode(year, countryCode);
        if (targetHolidays.isEmpty()) {
            throw new CustomLogicException(
                    ErrorCode.NOT_FOUND,
                    "해당하는 연도, 국가코드의 공휴일 목록을 찾을 수 없습니다."
            );
        }

        List<Long> holidayIds = getHolidayIds(targetHolidays);

        holidayTypeService.deleteHolidayTypes(holidayIds);
        holidayCountyService.deleteHolidayCounties(holidayIds);

        holidayRepository.deleteAll(targetHolidays);
    }

    @Transactional(readOnly = true)
    public PageResponse<HolidayListReadResponse> searchHolidays(
            Integer year,
            String countryCode,
            HolidayReadRequest request
    ) {

        validateDateYear(year, request);

        Pageable pageable = PageRequest.of(
                request.page(),
                request.size(),
                Sort.by("date").ascending()
        );

        Page<Holiday> page = holidayRepository.searchHoliday(
                year,
                countryCode,
                request.type(),
                request.from(),
                request.to(),
                pageable
        );

        List<Long> holidayIds = getHolidayIds(page.getContent());
        Map<Long, List<String>> holidayCountyMap =
                holidayCountyService.getHolidayCountyMapByHolidayIds(holidayIds);
        Map<Long, List<String>> holidayTypeMap =
                holidayTypeService.getHolidayTypeMapByHolidayIds(holidayIds);

        Page<HolidayListReadResponse> respPage = page.map(holiday ->
                new HolidayListReadResponse(
                        holiday.getId(),
                        holiday.getDate(),
                        holiday.getLocalName(),
                        holiday.getName(),
                        holiday.getCountry().getCountryCode(),
                        holidayCountyMap.getOrDefault(holiday.getId(), List.of()),
                        holidayTypeMap.getOrDefault(holiday.getId(), List.of())
                )
        );

        return new PageResponse<>(respPage);
    }

    @Transactional
    public void upsertHolidays(Integer year, Country country) {
        String countryCode = country.getCountryCode();

        Map<String, Holiday> oldHolidayMap = getHolidayMapByYearAndCountryCode(year, countryCode);

        HolidayCreationResultDto holidayCreationResult = prepareHolidayCreationResult(
                Map.of(country.getCountryCode(), country),
                List.of(year)
        );

        Map<String, Holiday> replacementHolidayMap =
                toHolidayMapByUniqueKey(holidayCreationResult.holidays(), countryCode);

        Set<Holiday> insertedHolidays = new HashSet<>();
        for (Map.Entry<String, Holiday> entry : replacementHolidayMap.entrySet()) {
            String uniqueKey = entry.getKey();
            Holiday replacementHoliday = entry.getValue();
            if (oldHolidayMap.containsKey(uniqueKey)) {
                oldHolidayMap.get(uniqueKey).update(
                        replacementHoliday.getName(),
                        replacementHoliday.isFixed(),
                        replacementHoliday.isGlobal(),
                        replacementHoliday.getLaunchYear()
                );
                // TODO: 업데이트될 type, county도 처리 필요
            } else {
                insertedHolidays.add(replacementHoliday);
                // TODO: 새로 삽입될 type, county도 처리 필요
            }
        }

        holidayRepository.saveAll(insertedHolidays);

        Set<Holiday> deletedHolidays = new HashSet<>();
        for (Map.Entry<String, Holiday> entry : oldHolidayMap.entrySet()) {
            if (!replacementHolidayMap.containsKey(entry.getKey())) {
                deletedHolidays.add(entry.getValue());
            }
        }

        // TODO: 삭제될 type, county도 처리 필요
        holidayRepository.deleteAll(deletedHolidays);

    }

    private Map<String, Holiday> toHolidayMapByUniqueKey(
            Collection<Holiday> holidays,
            String countryCode
    ) {
        Map<String, Holiday> holidayMap = new HashMap<>();
        holidays.forEach(holiday ->
            holidayMap.put(
                    createUniqueKey(
                            holiday.getDate(),
                            holiday.getLocalName(),
                            countryCode
                    ),
                    holiday
            )
        );
        return holidayMap;
    }

    // key: uniqueKey, value: Holiday
    private Map<String, Holiday> getHolidayMapByYearAndCountryCode(
            Integer year,
            String countryCode
    ) {
        List<Holiday> holidays = holidayRepository.findByYearAndCountryCode(year, countryCode);
        if (holidays.isEmpty()) {
            return Map.of();
        }

        return toHolidayMapByUniqueKey(holidays, countryCode);
    }

    // 외부 api에서 공휴일 목록을 조회한 결과로 새 holiday 목록 생성
    // 연관된 holidayType, holidayCounty 저장을 위해 필요한 holidayTypesDtos, holidayCountiesDtos 반환
    private HolidayCreationResultDto prepareHolidayCreationResult(
            Map<String, Country> countries,
            List<Integer> years
    ) {
        // counties가 존재할 경우 외부 api 호출 시 데이터가 중복되어 처리되는 문제를 처리하기 위해 uniqueKeySet 사용
        Set<String> uniqueKeySet = new HashSet<>();
        Set<Holiday> holidays = new HashSet<>();
        List<HolidayTypesDto> holidayTypesDtos = new ArrayList<>();
        List<HolidayCountiesDto> holidayCountiesDtos = new ArrayList<>();

        getExternalHolidays(countries.keySet(), years).forEach(ext -> {
            String uniqueKey = createUniqueKey(
                    LocalDate.parse(ext.date()),
                    ext.localName(),
                    ext.countryCode()
            );
            if (!uniqueKeySet.contains(uniqueKey)) {
                uniqueKeySet.add(uniqueKey);
                Holiday holiday = Holiday.builder()
                        .date(LocalDate.parse(ext.date()))
                        .localName(ext.localName())
                        .name(ext.name())
                        .fixed(ext.fixed())
                        .global(ext.global())
                        .launchYear(ext.launchYear())
                        .country(countries.get(ext.countryCode()))
                        .build();

                holidays.add(holiday);
                if (ext.types() != null && !ext.types().isEmpty()) {
                    holidayTypesDtos.add(new HolidayTypesDto(holiday, ext.types()));
                }
                if (!ext.global() && ext.counties() != null) {
                    holidayCountiesDtos.add(new HolidayCountiesDto(uniqueKey, ext.counties()));
                }
            }
        });
        return new HolidayCreationResultDto(
                holidays,
                holidayCountiesDtos,
                holidayTypesDtos
        );
    }

    private List<Long> getHolidayIds(List<Holiday> holidays) {
        return holidays.stream()
                .map(Holiday::getId)
                .toList();
    }

    private String createUniqueKey(LocalDate date, String localName, String countryCode) {
        return date.toString() + "|" + localName + "|" + countryCode;
    }

    private List<ExternalHolidayResponse> getExternalHolidays(
            Set<String> countyCodes,
            List<Integer> years
    ) {
        List<ExternalHolidayResponse> externalHolidays = new ArrayList<>();

        for (int year : years) {
            for (String countryCode : countyCodes) {
                externalHolidays.addAll(externalApiClient.getExternalHolidays(year, countryCode));
            }
        }

        return externalHolidays;
    }

    private List<Integer> getYears() {
        int years = holidayProperties.getFetchYears();
        int thisYear = LocalDate.now().getYear();

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < years; i++) {
            result.add(thisYear - i);
        }
        return result;
    }

    private void validateDateYear(Integer year, HolidayReadRequest request) {
        if (request.from() != null && request.from().getYear() != year) {
            throw new CustomLogicException(ErrorCode.BAD_REQUEST, "잘못된 조회 시작 날짜 입니다.");
        }

        if (request.to() != null && request.to().getYear() != year) {
            throw new CustomLogicException(ErrorCode.BAD_REQUEST, "잘못된 조회 종료 날짜 입니다.");
        }
    }
}
