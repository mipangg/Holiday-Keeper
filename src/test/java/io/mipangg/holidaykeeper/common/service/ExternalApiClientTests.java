package io.mipangg.holidaykeeper.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.mipangg.holidaykeeper.common.dto.ExternalCountryResponse;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

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
    void getExternalCountries_success_test() {

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

}