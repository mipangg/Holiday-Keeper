package io.mipangg.holidaykeeper.domain.holiday.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(HolidayController.class)
class HolidayControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HolidayService holidayService;

    @Test
    @DisplayName("공휴일 저장 성공")
    void saveHolidays_success() throws Exception {

        mockMvc.perform(post("/holidays"))
                .andExpect(status().isCreated());

        verify(holidayService, times(1)).saveHolidaysInLastFiveYears();
    }
}