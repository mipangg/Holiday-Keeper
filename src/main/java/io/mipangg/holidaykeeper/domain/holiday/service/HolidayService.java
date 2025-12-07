package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.domain.holidayType.service.HolidayTypeService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 기존 데이터 삭제 후 새로 저장
    @Transactional
    public void updateHolidays(int year, String countryCode) {

        try {
            deleteHolidays(year, countryCode);
        } catch (IllegalArgumentException e) {
            log.info(
                    "삭제할 기존 데이터가 없어 skip 처리 되었습니다. year={}, countryCode={}",
                    year, countryCode
            );
        }

        getExternalHolidays(year, countryCode, countryService.getByCode(countryCode));
    }

    @Transactional
    public void deleteHolidays(int year, String countryCode) {
        Country targetCountry = countryService.getByCode(countryCode);

        List<Holiday> targetHolidays = holidayRepository.findByYearAndCountry(year, targetCountry);
        if (targetHolidays.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("%d년 %s에 해당하는 holiday를 찾을 수 없습니다.", year, countryCode)
            );
        }

        holidayTypeService.deleteHolidayTypes(targetHolidays);
        holidayCountyService.deleteHolidayCounties(targetHolidays);

        holidayRepository.deleteAll(targetHolidays);

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

        return holidayRepository.findByDateAndCountryAndNameAndIsGlobal(date, country, name, isGlobal)
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

}
