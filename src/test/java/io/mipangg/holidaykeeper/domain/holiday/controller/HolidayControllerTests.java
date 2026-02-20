package io.mipangg.holidaykeeper.domain.holiday.controller;

import static io.mipangg.holidaykeeper.util.TestUtil.genHolidayListReadResponses;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.mipangg.holidaykeeper.domain.common.PageResponse;
import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayListReadResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.HolidayReadRequest;
import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HolidayController.class)
class HolidayControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HolidayService holidayService;

    @MockitoBean
    private CountryService countryService;

    @Test
    @DisplayName("공휴일 데이터 적재 테스트")
    void createHolidaysTest() throws Exception {

        mockMvc.perform(post("/holidays"))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(countryService).saveCountries();
        verify(holidayService).saveHolidays(anyMap());
    }

    @Test
    @DisplayName("공휴일 데이터 삭제 테스트")
    void deleteHolidaysTest() throws Exception {

        int year = 2026;
        String countryCode = "KR";

        mockMvc.perform(delete("/holidays/{year}/{countryCode}", year, countryCode))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(holidayService).deleteHolidays(year, countryCode);
    }

    @Test
    @DisplayName("공휴일 목록 조회 테스트")
    void readHolidaysTest() throws Exception {

        int year = 2026;
        String countryCode = "KR";

        List<HolidayListReadResponse> content = genHolidayListReadResponses();
        Page<HolidayListReadResponse> page = new PageImpl<>(content, PageRequest.of(0, 20), 2);
        PageResponse<HolidayListReadResponse> resp = new PageResponse<>(page);

        when(holidayService.searchHolidays(eq(year), eq(countryCode), any(HolidayReadRequest.class))).thenReturn(resp);

        mockMvc.perform(get("/holidays/{year}/{countryCode}", year, countryCode)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].date").value("2026-01-01"))
                .andExpect(jsonPath("$.content[0].localName").value("새해"))
                .andExpect(jsonPath("$.content[0].name").value("New Year's Day"))
                .andExpect(jsonPath("$.content[0].country").value("South Korea"))
                .andExpect(jsonPath("$.content[1].date").value("2026-02-16"))
                .andExpect(jsonPath("$.content[1].localName").value("설날"))
                .andExpect(jsonPath("$.content[1].name").value("Lunar New Year"))
                .andExpect(jsonPath("$.content[1].country").value("South Korea"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andDo(print());

    }

    @Test
    @DisplayName("공휴일 데이터 재동기화 테스트")
    void upsertHolidaysTest() throws Exception {

        int year = 2026;
        String countryCode = "KR";

        mockMvc.perform(put("/holidays/{year}/{countryCode}", year, countryCode))
                .andExpect(status().isOk())
                .andDo(print());

        verify(holidayService).upsertHolidays(year, countryCode);

    }

}