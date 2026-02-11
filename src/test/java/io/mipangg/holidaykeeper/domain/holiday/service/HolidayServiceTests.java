package io.mipangg.holidaykeeper.domain.holiday.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.external.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTests {

    @InjectMocks
    private HolidayService holidayService;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private ExternalApiClient externalApiClient;

    @Test
    @DisplayName("공휴일 적재 기능 테스트")
    void saveHolidaysTest() {

        Map<String, Country> countries = Map.of(
                "BR", Country.builder().countryCode("BR").name("Brazil").build(),
                "CA", Country.builder().countryCode("CA").name("Canada").build(),
                "KR", Country.builder().countryCode("KR").name("South Korea").build()
        );

        // 최근 5년 계산하여 List<Integer>로 반환
        int thisYear = LocalDate.now().getYear();
        List<Integer> last5Years = List.of(
                thisYear,
                thisYear - 1,
                thisYear - 2,
                thisYear - 3,
                thisYear - 4
        );

        // countries로 countrycode 리스트를 생성
        List<String> countryCodes = List.of("BR", "CA", "KR");

        // 반복문을 사용하여 ExternalApiClient.getExternalHolidays(year, countryCode) 호출
        List<ExternalHolidayResponse> holidayResponses = List.of(
                new ExternalHolidayResponse(
                        "2026-01-01",
                        "Confraternização Universal",
                        "New Year's Day",
                        "BR",
                        false,
                        true,
                        null,
                        null,
                        List.of("Public")
                ),
                new ExternalHolidayResponse(
                        "2026-02-16",
                        "Louis Riel Day",
                        "Louis Riel Day",
                        "CA",
                        false,
                        false,
                        List.of("CA_MB"),
                        null,
                        List.of("Public")
                ),
                new ExternalHolidayResponse(
                        "2026-01-01",
                        "새해",
                        "New Year's Day",
                        "KR",
                        false,
                        true,
                        null,
                        null,
                        List.of("Public")
                )
        );
        when(externalApiClient.getExternalHolidays(anyInt(), anyString())).thenReturn(holidayResponses);

        when(holidayRepository.count()).thenReturn(0L);

        holidayService.saveHolidays(countries);

        // repo에 이미 저장된 데이터가 있는지 확인
        verify(holidayRepository).count();
        verify(externalApiClient, times(countryCodes.size() * last5Years.size()))
                .getExternalHolidays(anyInt(), anyString());

        // List<ExternalHolidayResponse> -> List<Holiday>, List<HolidayTypesDto>, List<HolidayCountiesDto>로 변환
        // holidayRepository에 List<Holiday> 한번에 저장
        verify(holidayRepository).saveAll(anyList());

    }

    @Test
    @DisplayName("holidayRepo에 이미 저장된 데이터가 있는 경우 409 발생 테스트")
    void saveHolidays409FailTest() {

        when(holidayRepository.count()).thenReturn(3L);

        assertThatThrownBy(
                () -> {
                    holidayService.saveHolidays(Map.of());
                }
        ).isInstanceOf(CustomLogicException.class);

    }

}