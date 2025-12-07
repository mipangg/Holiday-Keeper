package io.mipangg.holidaykeeper.domain.holiday.util;

import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayCounty;
import io.mipangg.holidaykeeper.domain.holiday.entity.HolidayType;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;


@UtilityClass
public class HolidayFormatter {

    public static List<String> getHolidayCountyNames(List<HolidayCounty> holidayCounties) {
        if (holidayCounties == null || holidayCounties.isEmpty()) {
            return List.of();
        }

        List<String> holidayCountyNames = new ArrayList<>();
        for (HolidayCounty holidayCounty : holidayCounties) {
            holidayCountyNames.add(holidayCounty.getCounty().getName());
        }

        return holidayCountyNames;
    }

    public static List<String> getHolidayTypeNames(List<HolidayType> holidayTypes) {
        if (holidayTypes == null || holidayTypes.isEmpty()) {
            return List.of();
        }

        List<String> holidayTypeNames = new ArrayList<>();
        for (HolidayType holidayType : holidayTypes) {
            holidayTypeNames.add(holidayType.getType());
        }

        return holidayTypeNames;
    }

}
