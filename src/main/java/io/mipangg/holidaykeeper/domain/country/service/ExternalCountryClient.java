package io.mipangg.holidaykeeper.domain.country.service;

import io.mipangg.holidaykeeper.domain.country.dto.ExternalCountryResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ExternalCountryClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://date.nager.at/api/v3/")
            .build();

    public List<ExternalCountryResponse> getCountries() {
        return webClient.get()
                .uri("AvailableCountries")
                .retrieve()
                .bodyToFlux(ExternalCountryResponse.class)
                .collectList()
                .block();
    }
}
