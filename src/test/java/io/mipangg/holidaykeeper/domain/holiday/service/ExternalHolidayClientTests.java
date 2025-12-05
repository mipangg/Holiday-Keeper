package io.mipangg.holidaykeeper.domain.holiday.service;


import static org.assertj.core.api.Assertions.assertThat;

import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalHolidayClientTests {

    @InjectMocks
    private ExternalHolidayClient externalHolidayClient;

    @Test
    @DisplayName("year과 contryCode를 인자로 받아 외부 api로부터 공휴일 목록을 불러올 수 있다")
    void getHolidays_success_test() {

        int year = 2025;
        String countryCode = "KR";

        List<ExternalHolidayResponse> holidays =
                externalHolidayClient.getHolidays(year, countryCode);

        ExternalHolidayResponse resp1 = new ExternalHolidayResponse(
                "2025-01-01",
                "새해",
                "New Year's Day",
                "KR",
                false,
                true,
                null,
                null,
                List.of("Public")
        );
        ExternalHolidayResponse resp2 = new ExternalHolidayResponse(
                "2025-12-25",
                "크리스마스",
                "Christmas Day",
                "KR",
                false,
                true,
                null,
                null,
                List.of("Public")
        );

        assertThat(holidays).hasSize(15);
        assertThat(holidays).contains(resp1, resp2);

    }

}