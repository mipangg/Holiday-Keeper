package io.mipangg.holidaykeeper.domain.holiday.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayDetailResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidaySearchRequest;
import io.mipangg.holidaykeeper.domain.holiday.dto.PageResponse;
import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HolidayController.class)
class HolidayControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HolidayService holidayService;

    @Test
    @DisplayName("holiday를 정상적으로 등록 후 201을 반환한다")
    void saveHolidays_should_return_201() throws Exception {

        mockMvc.perform(post("/holidays"))
                .andExpect(status().isCreated());

        verify(holidayService, times(1)).syncHolidays();

    }

    @Test
    @DisplayName("year와 countryCode를 인자로 받아 특정 연도와 국가의 공휴일 리스트를 삭제하고 204를 반환한다")
    void deleteHolidays_should_return_204() throws Exception {

        int year = 2025;
        String countryCode = "KR";

        mockMvc.perform(delete("/holidays/{year}/{countryCode}", year, countryCode))
                .andExpect(status().isNoContent());

        verify(holidayService, times(1)).deleteHolidays(year, countryCode);

    }

    @Test
    @DisplayName("year와 countryCode를 인자로 받아 특정 연도와 국가의 공휴일 리스트를 덮어쓰고 200을 반환한다")
    void updateHolidays_should_return_200() throws Exception {

        int year = 2025;
        String countryCode = "KR";

        mockMvc.perform(put("/holidays/{year}/{countryCode}", year, countryCode))
                .andExpect(status().isOk());

        verify(holidayService, times(1)).upsertHolidays(year, countryCode);

    }

    @Test
    @DisplayName("year와 countryCode를 인자로 받아 특정 연도와 국가의 공휴일 리스트를 조회하고 200을 반환한다")
    void searchHolidays_should_return_200() throws Exception {

        int year = 2025;
        String countryCode = "KR";

        HolidayDetailResponse resp = new HolidayDetailResponse(
                LocalDate.parse("2025-01-01"),
                "새해",
                "New Year's Day",
                "South Korea",
                "KR",
                true,
                null,
                List.of("Public")
        );

        PageResponse<HolidayDetailResponse> pageResp = PageResponse.<HolidayDetailResponse>builder()
                .content(List.of(resp))
                .totalElements(1)
                .totalPages(1)
                .page(0)
                .size(20)
                .build();

        when(holidayService.searchHolidays(
                eq(year), eq(countryCode), any(HolidaySearchRequest.class))
        ).thenReturn(pageResp);

        mockMvc.perform(get("/holidays/{year}/{countryCode}", year, countryCode)
                        .param("page", "0")
                        .param("size", "20")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].localName")
                        .value("새해"))
                .andExpect(jsonPath("$.content[0].name")
                        .value("New Year's Day"))
                .andExpect(jsonPath("$.content[0].countryCode")
                        .value("KR"));

        verify(holidayService, times(1))
                .searchHolidays(eq(year), eq(countryCode), any(HolidaySearchRequest.class));

    }

}