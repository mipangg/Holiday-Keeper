package io.mipangg.holidaykeeper.domain.country.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.mipangg.holidaykeeper.domain.country.dto.ExternalCountryResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalCountryClientTests {

    @InjectMocks
    private ExternalCountryClient externalCountryClient;

    @Test
    @DisplayName("외부 api에서 country 목록을 불러올 수 있다")
    void getCountries_success_test() {

        List<ExternalCountryResponse> countries = externalCountryClient.getCountries();

        ExternalCountryResponse resp1
                = new ExternalCountryResponse("AD", "Andorra");
        ExternalCountryResponse resp2
                = new ExternalCountryResponse("ZW", "Zimbabwe");

        assertThat(countries).hasSize(119);
        assertThat(countries).contains(resp1, resp2);

    }

}