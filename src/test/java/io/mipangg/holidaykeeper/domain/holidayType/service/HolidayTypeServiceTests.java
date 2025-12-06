package io.mipangg.holidaykeeper.domain.holidayType.service;

import static io.mipangg.holidaykeeper.util.TestUtils.getHoliday;
import static io.mipangg.holidaykeeper.util.TestUtils.getHolidayCanada;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holidayType.entity.HolidayType;
import io.mipangg.holidaykeeper.domain.holidayType.repository.HolidayTypeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidayTypeServiceTests {

    @InjectMocks
    private HolidayTypeService holidayTypeService;

    @Mock
    private HolidayTypeRepository holidayTypeRepository;

    @Test
    @DisplayName("동일한 holidayType이 없으면 저장한다")
    void save_success_test() {

        List<String> holidayTypes = of("Public");
        Holiday holiday = getHoliday();

        when(holidayTypeRepository.findByTypeAndHoliday_Id(anyString(), anyLong()))
                .thenReturn(Optional.empty());

        holidayTypeService.saveIfNotExists(holidayTypes, holiday);

        verify(holidayTypeRepository, times(1))
                .findByTypeAndHoliday_Id("Public",holiday.getId());
        verify(holidayTypeRepository, times(1)).save(any(HolidayType.class));
    }

    @Test
    @DisplayName("이미 동일한 holidayType이 존재하면 저장하지 않는다")
    void save_test_case_holidayType_already_exist() {

        Holiday holiday = getHoliday();
        HolidayType holidayType = HolidayType.builder()
                .type("Public")
                .holiday(holiday)
                .build();

        when(holidayTypeRepository.findByTypeAndHoliday_Id(anyString(), anyLong()))
                .thenReturn(Optional.of(holidayType));


        holidayTypeService.saveIfNotExists(of("Public"), holiday);

        verify(holidayTypeRepository, times(1))
                .findByTypeAndHoliday_Id("Public",holiday.getId());
        verify(holidayTypeRepository, never()).save(any(HolidayType.class));

    }

}