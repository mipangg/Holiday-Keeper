package io.mipangg.holidaykeeper.domain.holidayCounty.service;

import io.mipangg.holidaykeeper.domain.holidayCounty.repository.HolidayCountyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidayCountyServiceTests {

    @InjectMocks
    private HolidayCountyService holidayCountyService;

    @Mock
    private HolidayCountyRepository holidayCountyRepository;

    @Test
    @DisplayName("holidayCounty 저장 기능 테스트")
    void saveHolidayCountiesTest() {


    }

}