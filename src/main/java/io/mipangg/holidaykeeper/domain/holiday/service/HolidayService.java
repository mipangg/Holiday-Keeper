package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayCountiesDto;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayTypesDto;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.external.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import io.mipangg.holidaykeeper.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Transactional
    public void saveHolidays(Map<String, Country> countries) {

        if (holidayRepository.count() > 0L) {
            throw new CustomLogicException(ErrorCode.CONFLICT, "Holiday 테이블에 이미 데이터가 존재합니다.");
        }

        List<ExternalHolidayResponse> externalHolidays = getExternalHolidays(
                getLast5Years(),
                countries.keySet()
        );

        List<Holiday> holidays = new ArrayList<>();
        List<HolidayCountiesDto> holidayCountiesDtos = new ArrayList<>();
        List<HolidayTypesDto> holidayTypesDtos = new ArrayList<>();
        externalHolidays.forEach(ext -> {
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
            holidayCountiesDtos.add(new HolidayCountiesDto(holiday, ext.counties()));
            holidayTypesDtos.add(new HolidayTypesDto(holiday, ext.types()));
        });

        // holidayCounty service에 List<HolidayCountiesDto> 넘겨 저장
        // holidayType service에 List<HolidayTypesDto> 넘겨 저장
        holidayRepository.saveAll(holidays);
    }

    // 외부 api에서 공휴일 정보를 조회 후 반환
    private List<ExternalHolidayResponse> getExternalHolidays(
            List<Integer> last5Years,
            Set<String> countryCodes
    ) {
        List<ExternalHolidayResponse> externalHolidays = new ArrayList<>();
        for (Integer year : last5Years) {
            for (String countryCode : countryCodes) {
                externalHolidays.addAll(externalApiClient.getExternalHolidays(year, countryCode));
            }
        }

        return externalHolidays;
    }

    private List<Integer> getLast5Years() {
        List<Integer> last5Years = new ArrayList<>();
        int thisYear = LocalDate.now().getYear();
        for (int year = thisYear; year > thisYear - 5; year--) {
            last5Years.add(year);
        }

        return last5Years;
    }
}
