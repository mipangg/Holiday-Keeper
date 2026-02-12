package io.mipangg.holidaykeeper.domain.holiday.controller;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.mipangg.holidaykeeper.domain.country.service.CountryService;
import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
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

}