package io.mipangg.holidaykeeper.domain.holiday.service;

import static io.mipangg.holidaykeeper.util.TestUtils.getCountiesCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getCountryCanada;
import static io.mipangg.holidaykeeper.util.TestUtils.getExternalHolidayResponses;
import static io.mipangg.holidaykeeper.util.TestUtils.getHolidayCanada;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayDetailResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidaySearchRequest;
import io.mipangg.holidaykeeper.domain.holiday.dto.PageResponse;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTests {

    @InjectMocks
    private HolidayService holidayService;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private ExternalHolidayClient externalHolidayClient;

    @Mock
    private CountryService countryService;

    @Mock
    private HolidayTypeService holidayTypeService;

    @Mock
    private HolidayCountyService holidayCountyService;

    @Test
    @DisplayName("최근 5년의 공휴일을 외부 api에 수집하여 저장할 수 있다")
    void syncHolidays_success_test() {

        Map<String, Country> countryMap = Map.of("Canada", getCountryCanada());
        List<ExternalHolidayResponse> externalHolidays = getExternalHolidayResponses();

        Holiday holiday = getHolidayCanada();

        when(countryService.findAll()).thenReturn(countryMap);
        when(externalHolidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(externalHolidays);
        when(holidayRepository.findByDateAndCountryAndNameAndIsGlobal(
                any(LocalDate.class), any(Country.class), anyString(), anyBoolean()
        ))
                .thenReturn(Optional.empty());
        when(holidayRepository.save(any(Holiday.class))).thenReturn(holiday);

        holidayService.syncHolidays();

        verify(holidayCountyService, times(6))
                .saveIfNotExists(anyList(), any(Country.class), eq(holiday));
        verify(holidayTypeService, times(6))
                .saveIfNotExists(anyList(), eq(holiday));
        verify(holidayRepository, times(6))
                .findByDateAndCountryAndNameAndIsGlobal(
                        any(LocalDate.class), any(Country.class), anyString(), anyBoolean()
                );
        verify(holidayRepository, times(6)).save(any(Holiday.class));

    }

    @Test
    @DisplayName("year과 countryCode를 인자로 받아 특정 holiday 리스트를 삭제할 수 있다")
    void deleteHolidays_success_test() {

        int year = 2025;
        String countryCode = "CA";
        List<Holiday> targetHolidays = List.of(getHolidayCanada());

        when(holidayRepository.findByYearAndCountryCode(year, countryCode)).thenReturn(
                targetHolidays);

        holidayService.deleteHolidays(year, countryCode);

        verify(holidayRepository, times(1)).deleteAll(targetHolidays);

    }

    @Test
    @DisplayName("year과 countryCode에 속하는 holiday 리스트가 없는데 삭제를 시도하면 예외가 발생한다")
    void deleteHolidays_fail_test() {

        int year = 2019;
        String countryCode = "CA";

        when(holidayRepository.findByYearAndCountryCode(year, countryCode)).thenReturn(List.of());

        assertThatThrownBy(
                () -> {
                    holidayService.deleteHolidays(year, countryCode);
                }
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        String.format("%d년 %s에 해당하는 holiday를 찾을 수 없습니다.", year, countryCode)
                );
    }

    @Test
    @DisplayName("외부 api를 재호출 했을 때 DB에 없는 데이터면 Insert 한다")
    void upsertHolidays_insert_success_test() {

        int year = 2025;
        String countryCode = "CA";
        List<ExternalHolidayResponse> externalHolidays = getExternalHolidayResponses();
        Country country = getCountryCanada();

        when(externalHolidayClient.getHolidays(year, countryCode)).thenReturn(externalHolidays);
        when(holidayRepository.findByYearAndCountryCode(year, countryCode)).thenReturn(List.of());
        when(countryService.getByCode(countryCode)).thenReturn(country);

        holidayService.upsertHolidays(year, countryCode);

        verify(holidayRepository, times(1)).saveAll(anyList());
        verify(holidayRepository, never()).deleteAll(anyList());

    }

    @Test
    @DisplayName("외부 api를 재호출 했을 때 DB에만 있는 데이터면 Delete 한다")
    void upsertHolidays_delete_success_test() {

        int year = 2025;
        String countryCode = "CA";
        Holiday holiday = getHolidayCanada();
        Country country = getCountryCanada();

        when(externalHolidayClient.getHolidays(year, countryCode)).thenReturn(List.of());
        when(holidayRepository.findByYearAndCountryCode(year, countryCode))
                .thenReturn(List.of(holiday));
        when(countryService.getByCode(countryCode)).thenReturn(country);

        holidayService.upsertHolidays(year, countryCode);

        verify(holidayRepository, never()).saveAll(anyList());
        verify(holidayRepository, times(1)).deleteAll(anyList());

    }

    @Test
    @DisplayName("외부 api를 재호출 했을 때 DB, 외부 api 둘 다 있는 데이터면 update 한다")
    void upsertHolidays_update_success_test() {

        int year = 2025;
        String countryCode = "CA";
        Holiday holiday = getHolidayCanada();
        List<ExternalHolidayResponse> externalHolidays = getExternalHolidayResponses();
        Country country = getCountryCanada();

        when(externalHolidayClient.getHolidays(year, countryCode))
                .thenReturn(externalHolidays);
        when(holidayRepository.findByYearAndCountryCode(year, countryCode))
                .thenReturn(List.of(holiday));
        when(countryService.getByCode(countryCode)).thenReturn(country);

        holidayService.upsertHolidays(year, countryCode);

        verify(holidayRepository, never()).saveAll(anyList());
        verify(holidayRepository, never()).deleteAll(anyList());

    }

    @Test
    @DisplayName("year과 countryCode를 인자로 받아 공휴일 리스트를 조회하고 PageResponse로 반환한다")
    void searchHoliday_seccess_test() {

        int year = 2025;
        String countryCode = "KR";

        HolidaySearchRequest req =
                new HolidaySearchRequest(0, 20, null, null, null);

        Holiday holiday = getHolidayCanada();

        Page<Holiday> page = new PageImpl<>(
                List.of(holiday),
                PageRequest.of(0, 20),
                1
        );

        List<County> counties = getCountiesCanada();
        List<HolidayCounty> holidayCounties = counties.stream()
                .map(c -> HolidayCounty.builder()
                        .county(c)
                        .holiday(holiday)
                        .build())
                .toList();

        HolidayType holidayType = HolidayType.builder()
                .type("Public")
                .holiday(holiday)
                .build();

        when(holidayRepository.searchHolidays(
                eq(year), eq(countryCode),
                any(), any(), any(),
                any(Pageable.class)
        )).thenReturn(page);
        when(holidayCountyService.findByHolidays(anyList()))
                .thenReturn(Map.of(holiday.getId(), holidayCounties));
        when(holidayTypeService.findHolidayTypes(anyList()))
                .thenReturn(Map.of(holiday.getId(), List.of(holidayType)));

        PageResponse<HolidayDetailResponse> pageResp =
                holidayService.searchHolidays(year, countryCode, req);

        assertThat(pageResp.content()).hasSize(1);

        HolidayDetailResponse resp = pageResp.content().get(0);

        assertThat(resp.date()).isEqualTo(LocalDate.of(2025, 2, 17));
        assertThat(resp.localName()).isEqualTo("Family Day");
        assertThat(resp.name()).isEqualTo("Family Day");
        assertThat(resp.country()).isEqualTo("Canada");
        assertThat(resp.countryCode()).isEqualTo("CA");
        assertThat(resp.counties())
                .containsExactlyInAnyOrder("CA-AB", "CA-BC", "CA-NB", "CA-ON", "CA-SK");
        assertThat(resp.types())
                .containsExactly("Public");

        assertThat(pageResp.page()).isEqualTo(0);
        assertThat(pageResp.size()).isEqualTo(20);
        assertThat(pageResp.totalElements()).isEqualTo(1);

        verify(holidayRepository).searchHolidays(
                eq(year), eq(countryCode),
                any(), any(), any(),
                any(Pageable.class)
        );
        verify(holidayCountyService).findByHolidays(anyList());
        verify(holidayTypeService).findHolidayTypes(anyList());

    }

}