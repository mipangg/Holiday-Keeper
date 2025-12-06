package io.mipangg.holidaykeeper.domain.holidayType.service;

import io.mipangg.holidaykeeper.domain.holidayType.repository.HolidayTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayTypeService {

    private final HolidayTypeRepository holidayTypeRepository;

    // List<String> Types, long holiday_id를 인자로 받음 -> repo에 없으면 저장

}
