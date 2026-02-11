package io.mipangg.holidaykeeper.domain.county.service;


import io.mipangg.holidaykeeper.domain.county.repository.CountyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CountyServiceTests {

    @InjectMocks
    private CountyService countyService;

    @Mock
    private CountyRepository countyRepository;

    @Test
    @DisplayName("county가 이미 존재하면 반환하고 없으면 저장하는 기능 테스트")
    void getOrCreateCountiesTest() {

    }

}