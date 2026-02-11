package io.mipangg.holidaykeeper.domain.holiday.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "holiday")
@Getter
@Setter
public class HolidayProperties {

    private int fetchYears;

}
