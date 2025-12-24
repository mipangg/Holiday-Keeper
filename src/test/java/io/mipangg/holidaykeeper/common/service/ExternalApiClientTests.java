package io.mipangg.holidaykeeper.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.mipangg.holidaykeeper.common.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.common.dto.ExternalHolidayResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

class ExternalApiClientTests {

    public static MockWebServer mockWebServer;
    private ExternalApiClient externalApiClient;
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        objectMapper = new ObjectMapper();
        final String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        final WebClient webClient = WebClient.create(baseUrl);
        externalApiClient = new ExternalApiClient(webClient);
    }

    @Test
    @DisplayName("getExternalCountries를 호출하여 모든 국가 이름과 코드를 조회할 수 있다")
    void getExternalCountries_success_test() throws Exception {

        // 응답 체크
        List<ExternalCountryResponse> expected =
                List.of(new ExternalCountryResponse("CA", "Canada"));

        mockWebServer.enqueue(new MockResponse() // 순서대로 응답 반환
                .setBody(objectMapper.writeValueAsString(expected))
                .addHeader("Content-Type", "application/json"));

        List<ExternalCountryResponse> actual = externalApiClient.getExternalCountries();

        assertThat(actual).hasSize(1);
        assertThat(expected.getFirst().countryCode()).isEqualTo(actual.getFirst().countryCode());
        assertThat(expected.getFirst().name()).isEqualTo(actual.getFirst().name());

        // 요청 체크
        final RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertAll(
                () -> assertEquals("GET", recordedRequest.getMethod()),
                () -> assertEquals("/AvailableCountries", recordedRequest.getPath())
        );

    }

    @Test
    @DisplayName("getExternalHolidays를 호출하여 특정 국가의 휴일 정보를 조회할 수 있다")
    void getExternalHolidays_success_test() throws Exception {

        // 응답 체크
        List<ExternalHolidayResponse> expected =
                List.of(
                        new ExternalHolidayResponse(
                                LocalDate.of(2025, 1, 1),
                                "New Year's Day",
                                "New Year's Day",
                                "CA",
                                false,
                                true,
                                null,
                                null,
                                List.of("Public"))
                );

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(expected))
                .addHeader("Content-Type", "application/json"));

        List<ExternalHolidayResponse> actual =
                externalApiClient.getExternalHolidays(2025, "CA");

        assertThat(actual).hasSize(1);
        assertThat(expected.getFirst().date()).isEqualTo(actual.getFirst().date());
        assertThat(expected.getFirst().localName()).isEqualTo(actual.getFirst().localName());
        assertThat(expected.getFirst().name()).isEqualTo(actual.getFirst().name());
        assertThat(expected.getFirst().countryCode()).isEqualTo(actual.getFirst().countryCode());

        // 요청 체크
        final RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertAll(
                () -> assertEquals("GET", recordedRequest.getMethod()),
                () -> assertEquals("/PublicHolidays/2025/CA", recordedRequest.getPath())
        );
    }

}