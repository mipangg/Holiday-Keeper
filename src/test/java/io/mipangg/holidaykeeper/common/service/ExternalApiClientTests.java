package io.mipangg.holidaykeeper.common.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.mipangg.holidaykeeper.external.dto.ExternalCountryResponse;
import io.mipangg.holidaykeeper.external.dto.ExternalHolidayResponse;
import io.mipangg.holidaykeeper.external.service.ExternalApiClient;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class ExternalApiClientTests {

    private static MockWebServer mockWebServer;
    private ExternalApiClient externalApiClient;

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
        final String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        final WebClient webClient = WebClient.create(baseUrl);
        externalApiClient = new ExternalApiClient(webClient);
    }

    @Test
    @DisplayName("외부 api를 호출하여 국가 목록을 성공적으로 불러올 수 있다")
    void getExternalCountriesTest() {

        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                                 [
                                        {
                                            "countryCode": "BR",
                                            "name": "Brazil"
                                        },
                                        {
                                            "countryCode": "KR",
                                            "name": "South Korea"
                                        }
                                 ]
                        """)
                .addHeader("Content-Type", "application/json"));

        final List<ExternalCountryResponse> countries = externalApiClient.getExternalCountries();

        assertThat(countries.getFirst().countryCode()).isEqualTo("BR");
        assertThat(countries.getFirst().name()).isEqualTo("Brazil");
        assertThat(countries.getLast().countryCode()).isEqualTo("KR");
        assertThat(countries.getLast().name()).isEqualTo("South Korea");

    }

    @Test
    @DisplayName("외부 api를 호출하여 공휴일 목록을 성공적으로 불러올 수 있다")
    void getExternalHolidaysTest() {

        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                                [
                                        {
                                            "date": "2026-01-01",
                                            "localName": "New Year's Day",
                                            "name": "New Year's Day",
                                            "countryCode": "CA",
                                            "fixed": false,
                                            "global": true,
                                            "counties": null,
                                            "launchYear": null,
                                            "types": [
                                               "Public"
                                            ]
                                        },
                                        {
                                             "date": "2026-02-16",
                                             "localName": "Louis Riel Day",
                                             "name": "Louis Riel Day",
                                             "countryCode": "CA",
                                             "fixed": false,
                                             "global": false,
                                             "counties": [
                                             "CA-MB"
                                             ],
                                             "launchYear": null,
                                             "types": [
                                                "Public"
                                             ]
                                        }
                                ]
                        """)
                .addHeader("Content-Type", "application/json"));

        final List<ExternalHolidayResponse> holidays =
                externalApiClient.getExternalHolidays(2026, "CA");

        assertThat(holidays.getFirst().date()).isEqualTo("2026-01-01");
        assertThat(holidays.getFirst().localName()).isEqualTo("New Year's Day");
        assertThat(holidays.getLast().date()).isEqualTo("2026-02-16");
        assertThat(holidays.getLast().localName()).isEqualTo("Louis Riel Day");

    }

}