package io.mipangg.holidaykeeper.domain.holidayType.entity;

import io.mipangg.holidaykeeper.domain.common.BaseEntity;
import io.mipangg.holidaykeeper.domain.holiday.entity.Holiday;
import io.mipangg.holidaykeeper.domain.type.entity.Type;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "holiday_type",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_holiday_type_holiday_id_type_id", // 제약조건 이름
                        columnNames = {"holiday_id", "type_id"} // 유니크 제약 걸 컬럼들
                )
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HolidayType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id", nullable = false)
    private Holiday holiday;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;

}
