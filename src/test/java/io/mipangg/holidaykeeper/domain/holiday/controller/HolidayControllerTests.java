package io.mipangg.holidaykeeper.domain.holiday.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.mipangg.holidaykeeper.domain.holiday.service.HolidayService;
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

}