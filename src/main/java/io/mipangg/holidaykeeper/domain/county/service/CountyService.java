package io.mipangg.holidaykeeper.domain.county.service;

import io.mipangg.holidaykeeper.domain.county.repository.CountyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountyService {

    private final CountyRepository countyRepository;


}
