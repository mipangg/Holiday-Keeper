package io.mipangg.holidaykeeper.domain.holiday.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.mipangg.holidaykeeper.domain.country.entity.QCountry;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.holiday.entity.QHoliday;
import io.mipangg.holidaykeeper.domain.holidayType.entity.QHolidayType;
import io.mipangg.holidaykeeper.domain.type.entity.QType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Holiday> searchHoliday(
            Integer year,
            String countryCode,
            String typeName,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {

        QHoliday holiday = QHoliday.holiday;
        QHolidayType holidayType = QHolidayType.holidayType;
        QType type = QType.type1;
        QCountry country = QCountry.country;

        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        // ID 페이징
        List<Tuple> tuples = queryFactory
                .select(holiday.id, holiday.date) // date 기준 오름차순 정렬을 위해 date도 추가
                .from(holiday)
                .join(holiday.country, country)
                .leftJoin(holidayType).on(holidayType.holiday.eq(holiday))
                .leftJoin(type).on(holidayType.type.eq(type))
                .where(
                        holiday.date.between(startOfYear, endOfYear),
                        country.countryCode.eq(countryCode),
                        typeEq(typeName),
                        fromGoe(from),
                        toLoe(to)
                )
                .orderBy(holiday.date.asc())
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> ids = new ArrayList<>();
        for (Tuple tuple : tuples) {
            ids.add(tuple.get(holiday.id));
        }

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        // fetch join
        List<Holiday> content = queryFactory
                .selectFrom(holiday)
                .join(holiday.country, country).fetchJoin() // country만 fetch
                .where(holiday.id.in(ids))
                .orderBy(holiday.date.asc())
                .fetch();

        // count
        Long total = queryFactory
                .select(holiday.countDistinct())
                .from(holiday)
                .join(holiday.country, country)
                .leftJoin(holidayType).on(holidayType.holiday.eq(holiday))
                .leftJoin(type).on(holidayType.type.eq(type))
                .where(
                        holiday.date.between(startOfYear, endOfYear),
                        country.countryCode.eq(countryCode),
                        typeEq(typeName),
                        fromGoe(from),
                        toLoe(to)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression typeEq(String typeName) {
        return typeName == null ? null : QType.type1.type.eq(typeName);
    }

    private BooleanExpression fromGoe(LocalDate from) {
        return from == null ? null : QHoliday.holiday.date.goe(from);
    }

    private BooleanExpression toLoe(LocalDate to) {
        return to == null ? null : QHoliday.holiday.date.loe(to);
    }

}
