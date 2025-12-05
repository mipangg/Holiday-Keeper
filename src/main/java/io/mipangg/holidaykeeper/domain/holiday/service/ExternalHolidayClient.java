package io.mipangg.holidaykeeper.domain.holiday.service;

import io.mipangg.holidaykeeper.domain.country.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.domain.holiday.dto.ExternalHolidayResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ExternalHolidayClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://date.nager.at/api/v3")
            .build();

    public List<ExternalHolidayResponse> getHolidays(int year, String countryCode) {
        return webClient.get()
                .uri("/PublicHolidays/{year}/{countryCode}", year, countryCode)
                .retrieve()
                .bodyToFlux(ExternalHolidayResponse.class)
                .collectList()
                .block();
    }

}
