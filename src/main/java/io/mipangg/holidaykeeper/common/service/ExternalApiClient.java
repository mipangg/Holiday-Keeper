package io.mipangg.holidaykeeper.common.service;

import io.mipangg.holidaykeeper.common.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.common.dto.ExternalHolidayResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ExternalApiClient {

    private final WebClient webClient;

    public ExternalApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<ExternalCountryResponse> getExternalCountries() {
        return webClient.get().uri("/AvailableCountries")
                .retrieve()
                .bodyToFlux(ExternalCountryResponse.class)
                .collectList()
                .block();
    }

    public List<ExternalHolidayResponse> getExternalHolidays(int year, String countryCode)
    throws IllegalArgumentException {
        return webClient.get().uri("/PublicHolidays/{year}/{countryCode}", year, countryCode)
                .retrieve()
                .bodyToFlux(ExternalHolidayResponse.class)
                .collectList()
                .block();
    }
}
