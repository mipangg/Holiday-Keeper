package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.domain.holiday.util.HolidayFormatter.getHolidayCountyNames;
import static io.mipangg.holidaykeeper.domain.holiday.util.HolidayFormatter.getHolidayTypeNames;
import static io.mipangg.holidaykeeper.domain.holiday.dto.HolidayDetailResponse.toHolidayDetailResponse;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayDetailResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidaySearchRequest;
import io.mipangg.holidaykeeper.domain.holiday.dto.PageResponse;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    public void upsertHolidays(int year, String countryCode) {
        List<ExternalHolidayResponse> externalHolidays =
                externalHolidayClient.getHolidays(year, countryCode);
        List<Holiday> holidays = holidayRepository.findByYearAndCountryCode(year, countryCode);

        Country country = countryService.getByCode(countryCode);

        Map<String, Holiday> holidayMap = new HashMap<>();
        holidays.forEach(holiday -> {
            String key = createKey(holiday.getDate().toString(), holiday.getName());
            holidayMap.put(key, holiday);
        });

        Map<String, ExternalHolidayResponse> externalHolidayMap = new HashMap<>();
        externalHolidays.forEach(externalHoliday -> {
            String key = createKey(externalHoliday.date(), externalHoliday.name());
            externalHolidayMap.put(key, externalHoliday);
        });

        List<Holiday> toInsert = new ArrayList<>();
        List<Holiday> toDelete = new ArrayList<>();

        for (ExternalHolidayResponse externalHoliday : externalHolidays) {
            String key = createKey(externalHoliday.date(), externalHoliday.name());
            Holiday holiday = holidayMap.get(key);

            if (holiday == null) {
                Holiday newHoliday = Holiday.builder()
                        .date(LocalDate.parse(externalHoliday.date()))
                        .localName(externalHoliday.localName())
                        .name(externalHoliday.name())
                        .country(country)
                        .isFixed(externalHoliday.fixed())
                        .isGlobal(externalHoliday.global())
                        .launchYear(externalHoliday.launchYear())
                        .build();

                toInsert.add(newHoliday);

            } else {
                holiday.update(externalHoliday);
            }
        }

        // 외부 데이터에는 없는데 db에 있으면 삭제
        for (Holiday holiday : holidays) {
            String key = createKey(holiday.getDate().toString(), holiday.getName());
            if (!externalHolidayMap.containsKey(key)) {
                toDelete.add(holiday);
            }
        }

        if (!toInsert.isEmpty()) {
            holidayRepository.saveAll(toInsert);
        }
        if (!toDelete.isEmpty()) {
            holidayRepository.deleteAll(toDelete);
        }

    }

    @Transactional
    public void deleteHolidays(int year, String countryCode) {
        List<Holiday> targetHolidays = holidayRepository.findByYearAndCountryCode(year,countryCode);
        if (targetHolidays.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("%d년 %s에 해당하는 holiday를 찾을 수 없습니다.", year, countryCode)
            );
        }

        holidayTypeService.deleteHolidayTypes(targetHolidays);
        holidayCountyService.deleteHolidayCounties(targetHolidays);

        holidayRepository.deleteAll(targetHolidays);

    }

    @Transactional(readOnly = true)
    public PageResponse<HolidayDetailResponse> searchHolidays(
            int year,
            String countryCode,
            HolidaySearchRequest request
    ) {
        Pageable pageable = PageRequest.of(request.page(), request.size());

        Page<Holiday> page = holidayRepository.searchHolidays(
                year,
                countryCode,
                request.from(),
                request.to(),
                request.holidayType(),
                pageable
        );

        List<Holiday> holidays = page.getContent();
        // counties 정보 가져오기
        Map<Long, List<HolidayCounty>> holidayCountyMap =
                holidayCountyService.findByHolidays(holidays);

        // types 정보 가져오기
        Map<Long, List<HolidayType>> holidayTypeMap = holidayTypeService.findHolidayTypes(holidays);

        List<HolidayDetailResponse> responseList = new ArrayList<>();

        for (Holiday holiday : holidays) {
            List<HolidayCounty> counties = holidayCountyMap.get(holiday.getId());
            List<HolidayType> types = holidayTypeMap.get(holiday.getId());

            responseList.add(toHolidayDetailResponse(
                    holiday,
                    getHolidayCountyNames(counties),
                    getHolidayTypeNames(types)
            ));
        }

        return PageResponse.from(page, responseList);
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
        String name = externalHoliday.name();
        boolean isGlobal = externalHoliday.global();

        return holidayRepository
                .findByDateAndCountryAndNameAndIsGlobal(date, country, name, isGlobal)
                .orElseGet(() ->
                        holidayRepository.save(
                                Holiday.builder()
                                        .date(date)
                                        .localName(externalHoliday.localName())
                                        .name(name)
                                        .isFixed(externalHoliday.fixed())
                                        .isGlobal(isGlobal)
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

    // holiday 날짜와 이름 조합으로 키를 생성하여 반환
    private String createKey(String date, String name) {
        return date + name;
    }

}
