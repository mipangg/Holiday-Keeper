package io.mipangg.holidaykeeper.external.service;

import io.mipangg.holidaykeeper.external.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.external.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.global.exception.CustomLogicException;
import io.mipangg.holidaykeeper.global.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Service
public class ExternalApiClient {

    private final WebClient webClient;

    public ExternalApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<ExternalCountryResponse> getExternalCountries() {
        try {
            return webClient.get()
                    .uri("/AvailableCountries")
                    .retrieve()
                    .bodyToFlux(ExternalCountryResponse.class)
                    .collectList()
                    .block();
        } catch (WebClientException e) {
            throw new CustomLogicException(
                    ErrorCode.API_CALL_ERROR,
                    "국가 목록 조회 API 호출에 실패했습니다."
            );
        }

    }

    public List<ExternalHolidayResponse> getExternalHolidays(int year, String countryCode) {
        try {
            return webClient.get()
                    .uri("/PublicHolidays/{year}/{countryCode}", year, countryCode)
                    .retrieve()
                    .bodyToFlux(ExternalHolidayResponse.class)
                    .collectList()
                    .block();
        } catch (WebClientException e) {
            throw new CustomLogicException(
                    ErrorCode.API_CALL_ERROR,
                    "공휴일 목록 조회 API 호출에 실패했습니다."
            );
        }

    }

}
