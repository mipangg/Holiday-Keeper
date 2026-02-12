package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayCountiesDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
            throw new CustomLogicException(ErrorCode.CONFLICT, "Holiday 테이블에 이미 데이터가 존재합니다.");
        }

        // counties가 존재할 경우 외부 api 호출 시 데이터가 중복되어 처리되는 문제를 처리하기 위해 uniqueKeySet 사용
        Set<String> uniqueKeySet = new HashSet<>();
        Set<Holiday> holidays = new HashSet<>();
        List<HolidayTypesDto> holidayTypesDtos = new ArrayList<>();
        List<HolidayCountiesDto> holidayCountiesDtos = new ArrayList<>();

        getExternalHolidays(countries.keySet()).forEach(ext -> {
            String uniqueKey = createUniqueKey(ext);
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
                if (ext.counties() != null && !ext.counties().isEmpty()) {
                    holidayCountiesDtos.add(new HolidayCountiesDto(holiday, ext.counties()));
                }
            }
        });

        holidayRepository.saveAll(holidays);

        holidayTypeService.saveHolidayTypes(holidayTypesDtos);
        holidayCountyService.saveHolidayCounties(holidayCountiesDtos);

    }

    @Transactional
    public void deleteHolidays(Integer year, String countryCode) {
        List<Holiday> targetHolidays = holidayRepository.findByYearAndCountryCode(year, countryCode);
        if (targetHolidays.isEmpty()) {
            throw new CustomLogicException(
                    ErrorCode.NOT_FOUND,
                    "해당하는 연도, 국가코드의 공휴일 목록을 찾을 수 없습니다."
            );
        }
        holidayRepository.deleteAll(targetHolidays);
    }

    private static String createUniqueKey(ExternalHolidayResponse ext) {
        return ext.date() + "|" + ext.localName() + "|" + ext.countryCode();
    }

    private List<ExternalHolidayResponse> getExternalHolidays(Set<String> countyCodes) {
        List<ExternalHolidayResponse> externalHolidays = new ArrayList<>();

        for (int year : getYears()) {
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
}
