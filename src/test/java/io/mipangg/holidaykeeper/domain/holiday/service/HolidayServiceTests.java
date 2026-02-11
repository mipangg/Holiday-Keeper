package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.holiday.repository.HolidayRepository;
import io.mipangg.holidaykeeper.domain.holidayCounty.service.HolidayCountyService;
import io.mipangg.holidaykeeper.domain.holidayType.service.HolidayTypeService;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
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

    @Mock
    private HolidayCountyService holidayCountyService;

    @Mock
    private HolidayTypeService holidayTypeService;


}